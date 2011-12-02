package eu.ops.plugin.querymapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Message;

import org.bridgedb.BridgeDb;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import eu.larkc.core.data.DataFactory;
import eu.larkc.core.data.SetOfStatements;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SPARQLQueryImpl;
import eu.larkc.plugin.Plugin;

/**
 * <p>
 * Generated LarKC plug-in skeleton for
 * <code>eu.ops.plugin.querymapper.QueryMapper</code>. Use this class as an
 * entry point for your plug-in development.
 * </p>
 */
public class QueryMapper extends Plugin {

	private IDMapper mapper;
	public static String METABOLITE_DB = "/home/lupin/projects/jsaito.svn.bigcat/test/bridgedb-1.1.0-src/data/metabolites_100227.bridge";

	/**
	 * Constructor.
	 * 
	 * @param pluginUri
	 *            a URI representing the plug-in type, e.g.
	 *            <code>eu.larkc.plugin.myplugin.MyPlugin</code>
	 */
	public QueryMapper(URI pluginUri) {
		super(pluginUri);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Will be called when a message is send to the plug-in.
	 * 
	 * @param message
	 *            the message send to the plug-in
	 */
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
	}

	/**
	 * Called on plug-in initialisation. The plug-in instances are initialised
	 * on workflow initialisation.
	 * 
	 * @param workflowDescription
	 *            set of statements containing plug-in specific information
	 *            which might be needed for initialization (e.g. plug-in
	 *            parameters).
	 */
	@Override
	protected void initialiseInternal(SetOfStatements workflowDescription) {
		// TODO Auto-generated method stub
		BioDataSource.init();
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			mapper = BridgeDb
					.connect("idmapper-pgdb:" + METABOLITE_DB );

		} catch (IDMapperException ex) {
			logger.error("could not connect to mapper", ex);
		}

		logger.info("QueryMapper initialized. Hello World!");
	}

	/**
	 * @param url
	 *            Input URL for one identifier
	 * @return the ID substring of an Identifier's URL, assuming that the ID
	 *         substring is in the end
	 */
	private String urlToId(String url) {
		String returnValue = "";
		ArrayList<String> knownPrefixes = new ArrayList<String>();

		knownPrefixes.add(BioDataSource.KEGG_COMPOUND.getUrl(""));
		knownPrefixes.add(BioDataSource.CHEBI.getUrl(""));
		for (String prefix : knownPrefixes) {
			if (url.startsWith(prefix)) {
				returnValue = url.substring(prefix.length());
			}
		}
		return returnValue;
	}

	private Set<String> getIDs( String url ) {
		String idString = urlToId(url);
		Set<String> xrefs = new HashSet<String>();

		try {
			Xref src = new Xref(idString, BioDataSource.CHEBI);
			for (Xref x : mapper.mapID(src, BioDataSource.KEGG_COMPOUND)) {
				xrefs.add("<" + x.getUrl() + ">");
			}
			xrefs.add("<" + src.getUrl() + ">");
			return xrefs;
		} catch (IDMapperException ex) {
			logger.warn("failed to map", ex);
		}
		return xrefs;
	}

	/**
	 * Called on plug-in invokation. The actual "work" should be done in this
	 * method.
	 * 
	 * @param input
	 *            a set of statements containing the input for this plug-in
	 * 
	 * @return a set of statements containing the output of this plug-in
	 */
	@Override
	protected SetOfStatements invokeInternal(SetOfStatements input) {
		logger.info("QueryMapper working.");
		SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);

		String expandedQuery = "";

		if (query instanceof SPARQLQueryImpl) {
			StatementPatternCollector spc = new StatementPatternCollector();
			((SPARQLQueryImpl) query).getParsedQuery().getTupleExpr()
					.visit(spc);

			for (StatementPattern sp : spc.getStatementPatterns()) {
				Resource s = (Resource) sp.getSubjectVar().getValue();
				URI p = (URI) sp.getPredicateVar().getValue();
				Value o = (Value) sp.getObjectVar().getValue();

				Set<String> substitutedObjectIDs = new HashSet<String>();

				if (o == null) {
					substitutedObjectIDs.add("?" + sp.getObjectVar().getName());
				} else if (o instanceof Literal) {
					substitutedObjectIDs = getIDs(((Literal) o).stringValue());
				} else if (o instanceof URI) {
					substitutedObjectIDs = getIDs(((URI) o).toString());
				} else
					throw new IllegalArgumentException(
							"Unrecognized term in object position");

				Set<String> substitutedSubjectIDs = new HashSet<String>();
				if (s != null) {
					substitutedSubjectIDs = getIDs(((URI) s).toString());
				} else
					substitutedSubjectIDs.add("?"
							+ sp.getSubjectVar().getName());

				String predicate = (p == null) ? ("?" + sp.getPredicateVar()
						.getName()) : "<" + p.toString() + ">";

				for (String subRef : substitutedSubjectIDs) {
					for (String objRef : substitutedObjectIDs) {
						expandedQuery += "{" + subRef + " " + predicate + " "
								+ objRef + " .} UNION ";
					}
				}
				expandedQuery = expandedQuery.substring(0, expandedQuery
						.length() - 7);
			}
			String firstPart = query.toString().split(" where ")[0];
			expandedQuery = firstPart + " where {" + expandedQuery + "}";
		}
		
		logger.info(expandedQuery.toString());
		return new SPARQLQueryImpl(expandedQuery).toRDF();
	}

	/**
	 * Called on plug-in destruction. Plug-ins are destroyed on workflow
	 * deletion. Free an resources you might have allocated here.
	 */
	@Override
	protected void shutdownInternal() {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		QueryMapper queryMapper = new QueryMapper(null);
		queryMapper.initialiseInternal(null);

		queryMapper.invokeInternal(new SPARQLQueryImpl(
				"select * where { <http://www.ebi.ac.uk/chebi/searchId.do?chebiId=16811> ?p \"\"} limit 1").toRDF());
	}
}
