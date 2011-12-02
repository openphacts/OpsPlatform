package eu.ops.plugin.querymapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.helpers.StatementPatternCollector;

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
	public static URIImpl METABOLITE_DB = new URIImpl("http://ops.eu#bridgedb_mappings");

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
		String db = DataFactory.INSTANCE.extractObjectsForPredicate(workflowDescription, METABOLITE_DB).get(0);
		BioDataSource.init();
		try {
			Class.forName("org.bridgedb.rdb.IDMapperRdb");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			mapper = BridgeDb
					.connect("idmapper-pgdb:" + db );

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
	private Xref urlToId(String url) {
		Map<String,DataSource> knownPrefixes = new HashMap<String,DataSource>();

		knownPrefixes.put("http://chem2bio2rdf.org/kegg/resource/kegg_ligand/",BioDataSource.KEGG_COMPOUND);
		knownPrefixes.put("http://chem2bio2rdf.org/chebi/resource/chebi/CHEBI%3A",BioDataSource.CHEBI);
		logger.info("KnownPrefixes= " + knownPrefixes);
		for (Map.Entry<String,DataSource> prefix : knownPrefixes.entrySet()) {
			if (url.startsWith(prefix.getKey())) {
				String u=url.substring(prefix.getKey().length());
				logger.info("Identified url " + url + " in " + prefix);
				return new Xref(u,prefix.getValue());
			}
		}
		return null;
	}

	private Set<String> getIDs( String url ) {
		Xref xref = urlToId(url);
		Set<String> urls = new HashSet<String>();
		urls.add("<"+url+">");
		if (xref!=null) {			
			try {
				for (Xref x : mapper.mapID(xref, BioDataSource.KEGG_COMPOUND)) {
					logger.info("Mapping found: " + x );
					urls.add("<http://chem2bio2rdf.org/kegg/resource/kegg_ligand/" + x.getId() + ">");
					//urls.add("<" + x.getUrl() + ">");
				}
				return urls;
			} catch (IDMapperException ex) {
				logger.warn("failed to map", ex);
			}
		}
		return urls;
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
		//logger.info("QueryMapper working.");
		SPARQLQuery query = DataFactory.INSTANCE.createSPARQLQuery(input);

		logger.info("QueryMapper working. query =" + query.toString());
		
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
				} 
				else if (o instanceof Literal) {
					substitutedObjectIDs = new HashSet<String>();
					substitutedObjectIDs.add(o.toString());
				}
				else if (o instanceof URI) {
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
			String queryString = query.toString();
			String firstPart = queryString.split(" where ")[0];
			String lastPart=queryString.substring(queryString.lastIndexOf("}")+1, queryString.length());
			expandedQuery = firstPart + " where {" + expandedQuery + "}" + lastPart;
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
	//	queryMapper.initialiseInternal(null);

		queryMapper.invokeInternal(new SPARQLQueryImpl(
				"SELECT * where {<http://chem2bio2rdf.org/chebi/resource/chebi/CHEBI%3A242117> ?p ?o} LIMIT 10").toRDF());
	}



	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
