/*
   This file is part of the LarKC platform 
   http://www.larkc.eu/

   Copyright 2010 LarKC project consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.larkc.core.data;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
//import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import virtuoso.sesame2.driver.VirtuosoRepository;

//import com.clarkparsia.fourstore.sesame.FourStoreSail;
//import com.clarkparsia.fourstore.sesame.FourStoreSailRepository;

import eu.larkc.core.data.iterator.GraphQueryResultCloseableIterator;
import eu.larkc.core.data.iterator.RepositoryResultCloseableIterator;
import eu.larkc.core.query.SPARQLQuery;
import eu.larkc.core.query.SesameVariableBinding;

//import com.bigdata.rdf.sail.BigdataSail;
//import com.bigdata.rdf.sail.BigdataSailRepository;

/**
 * SesameRdfStoreConnectionImpl is class to implement a connection to local RDF
 * repository.
 * 
 * 
 */
public class SAILRdfStoreConnectionImpl implements RdfStoreConnection {

	private static Logger logger = LoggerFactory
			.getLogger(SAILRdfStoreConnectionImpl.class);

	protected final RepositoryConnection con;
	public static Repository myRepository = null;
	static int count = 0;

	/**
	 * Constructor
	 * 
	 * @param con
	 *            a connection to a sail repository
	 */
	public SAILRdfStoreConnectionImpl(RepositoryConnection con) {
		if (con == null) {
			throw new IllegalArgumentException("null!");
		}
		this.con = con;
	}

	public String getRepositoryClass() {
		if (myRepository == null)
			return "Repository not initialised";
		else
			return myRepository.getClass().getName();
	}
	
	/* Sesame in-memory 
	public static Repository getInMemoryRepository() {
		if (myRepository == null) {
			myRepository = new SailRepository(new MemoryStore());
			try {
				myRepository.initialize();

			} catch (RepositoryException e) {
				new RuntimeException(e);
			}
		}
		return myRepository;
	}*/

	
	/* Sesame HTTP*/
	public static Repository getSesameHttpRepository(String url, String repo) {
	if (myRepository == null) {
	        myRepository = new HTTPRepository(url, repo);
	        try {
	            myRepository.initialize();
	
	        } catch (RepositoryException e) {
				new RuntimeException(e);
	        }
		}
	    return myRepository;
	}
	
	/* 4store 
	public static Repository get4StoreRepository(URL url) { 
		if (myRepository == null) { 
			try { 
				FourStoreSail mySail = new FourStoreSail(url);
				mySail.initialize();
				myRepository = new FourStoreSailRepository(mySail); 
			} catch (Exception e) { 
				e.printStackTrace(); 
			new RuntimeException(e); 
			} 
		} 
		return myRepository; 
	}*/
	
	/* Virtuoso 
	public static Repository getVirtuosoRepository(){
		if (myRepository == null) { 
			try { 
				myRepository = new VirtuosoRepository("jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2","dba","dba");
				myRepository.initialize();
			} catch (Exception e) { 
				e.printStackTrace(); 
			new RuntimeException(e); 
			} 
		} 
		return myRepository; 
	}*/
	
	/* bigdata
	public static Repository getBigdataRepository() { 
		count++; 
		if (myRepository == null) { 
			logger.debug("need a repository"); 
			Properties p = new Properties();
			p.setProperty(BigdataSail.Options.STATEMENT_IDENTIFIERS, "false");
			//p.setProperty(BigdataSail.Options.ISOLATABLE_INDICES, "true");
			p.setProperty(BigdataSail.Options.TRUTH_MAINTENANCE, "true");
			//p.setProperty(BigdataSail.Options.AXIOMS_CLASS,"com.bigdata.rdf.axioms.NoAxioms");
			//p.setProperty(BigdataSail.Options.VOCABULARY_CLASS, "com.bigdata.rdf.vocab.NoVocabulary");
			p.setProperty(BigdataSail.Options.JUSTIFY, "false");
			p.setProperty(BigdataSail.Options.TEXT_INDEX, "false");
			p.setProperty(BigdataSail.Options.INITIAL_EXTENT, "209715200");
			//p.setProperty(BigdataSail.Options.MAXIMUM_EXTENT, "209715200");
			p.setProperty(BigdataSail.Options.FILE, "../test.jnl"); 
			try {
				myRepository = new BigdataSailRepository(new BigdataSail(p));
				myRepository.initialize();
			}
			catch (Exception e) { 
				e.printStackTrace(); 
				new RuntimeException(e); 
			} 
		}
		logger.debug("got a repository (count: "+count+")"); 
		if (myRepository==null)
			logger.debug("but it's null!!"); 
		return myRepository;
	}*/

	public Statement addStatement(Resource subj, URI pred, Value obj, URI graph) {
		try {
			Statement s = new StatementImpl(subj, pred, obj);
			logger.debug("Adding statement: " + s.toString());
			con.add(s, graph);
			con.commit();
			return s;
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public Statement addStatement(Resource subj, URI pred, Value obj,
			URI graph, URI... label) {
		try {
			ArrayList<URI> contexts = new ArrayList<URI>();
			contexts.addAll(Arrays.asList(label));
			contexts.add(graph);

			Statement s = new StatementImpl(subj, pred, obj);

			con.add(s, contexts.toArray(new URI[0]));
			con.commit();
			return s;
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public void addFile(InputStream file, RDFFormat format, String baseURI,
			URI graph) {
		try {
			con.add(file, baseURI, format, graph);
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean associateStatements(Resource subj, URI pred, Value obj,
			URI graph, URI... labels) {
		addStatement(subj, pred, obj, graph, labels);
		return true;
	}

	public boolean associateStatements(SPARQLQuery query, URI... ts) {
		if (query.isDescribe() == false && query.isConstruct() == false) {
			throw new IllegalArgumentException(
					"Only CONSTRUCT and DESCRIBE queries are supported!");
		}
		if (ts == null || query == null) {
			throw new IllegalArgumentException("null!");
		}
		SetOfStatements result = this.executeConstruct(query);
		CloseableIterator<Statement> iter = result.getStatements();
		int count = 0;
		while (iter.hasNext()) {
			Statement s = iter.next();
			try {
				con.add(s, ts);
			} catch (RepositoryException e) {
				throw new RuntimeException(e);
			}
			count = count + 1;
		}
		return count <= 0 ? false : true;
	}

	public void close() {
		try {
			con.close();
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public boolean isClosed() {
		try {
			return con.isOpen() == true ? false : true;
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public LabelledGroupOfStatements createLabelledGroupOfStatements(URI label) {
		return new LabelledGroupOfStatementsImpl(label, this, true);
	}

	public LabelledGroupOfStatements createLabelledGroupOfStatements() {
		return new LabelledGroupOfStatementsImpl(this);
	}

	public boolean deassociateStatements(Resource subj, URI pred, Value obj,
			URI graph, URI label) {
		try {
			ArrayList<URI> contexts = new ArrayList<URI>();
			contexts.addAll(Arrays.asList(label));
			contexts.add(graph);

			Statement s = new StatementImpl(subj, pred, obj);

			con.remove(s, contexts.toArray(new URI[0]));
			return true;
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public ValueFactory getValueFactory() {
		return con.getValueFactory();
	}

	public int removeStatement(Resource subj, URI pred, Value obj, URI graph) {
		try {
			con.remove(subj, pred, obj, graph);
			return 0;
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	public CloseableIterator<Statement> search(Resource subj, URI pred,
			Value obj, URI graph, URI label) {
		try {
			ArrayList<URI> contexts = new ArrayList<URI>();
			contexts.addAll(Arrays.asList(label));
			contexts.add(graph);

			return new RepositoryResultCloseableIterator<Statement>(
					(RepositoryResult<Statement>) con.getStatements(subj, pred,
							obj, true, contexts.toArray(new URI[0])));
		} catch (RepositoryException oe) {
			throw new RuntimeException(oe);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.SPARQLEndpoint#createDataSet(java.util.Collection,
	 * java.util.Collection)
	 */
	@Override
	public DataSet createDataSet(Collection<URI> defaultGraphs,
			Collection<URI> namedGraphs) {
		return new DataSetImpl(this, defaultGraphs, namedGraphs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.SPARQLEndpoint#createRdfGraph(org.openrdf.model.URI)
	 */
	@Override
	public RdfGraph createRdfGraph(URI graph) {
		return new RdfGraphDataSet(graph, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.SPARQLEndpoint#executeAsk(eu.larkc.core.query.SPARQLQuery
	 * )
	 */
	@Override
	public boolean executeAsk(SPARQLQuery query) {
		if (query == null) {
			throw new IllegalArgumentException("Null value is not supported!");
		}
		if (query.isAsk() == false) {
			throw new IllegalArgumentException(
					"Only ASK queries are supported!");
		}

		try {

			BooleanQuery gQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL,
					query.toString());
			boolean res = gQuery.evaluate();
			return res;

		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);

		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.larkc.core.data.SPARQLEndpoint#executeConstruct(eu.larkc.core.query
	 * .SPARQLQuery)
	 */
	@Override
	public SetOfStatements executeConstruct(SPARQLQuery query) {
		try {
			RepositoryResult<Statement> test = con.getStatements(null, null,
					null, true);
			while (test.hasNext()) {
				logger.debug(test.next().toString());

			}

			GraphQuery gQuery = con.prepareGraphQuery(QueryLanguage.SPARQL,
					query.toString());
			GraphQueryResult result = gQuery.evaluate();

			SetOfStatements sos = new SetOfStatementsImpl(
					new GraphQueryResultCloseableIterator<Statement>(result));
			logger.debug(sos.toString());

			return sos;

		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.larkc.core.data.SPARQLEndpoint#executeSelect(eu.larkc.core.query.
	 * SPARQLQuery)
	 */
	@Override
	public VariableBinding executeSelect(SPARQLQuery query) {
		try {
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					query.toString());
			SesameVariableBinding varbinding = new SesameVariableBinding();
			tupleQuery.evaluate(varbinding);
			return varbinding;

		} catch (QueryEvaluationException e) {
			throw new RuntimeException(e);
		} catch (RepositoryException e) {
			throw new RuntimeException(e);
		} catch (MalformedQueryException e) {
			throw new RuntimeException(e);
		} catch (TupleQueryResultHandlerException e) {
			throw new RuntimeException(e);

		}
	}
}
