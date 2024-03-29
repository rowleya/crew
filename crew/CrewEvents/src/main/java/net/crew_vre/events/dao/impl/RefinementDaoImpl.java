/**
 * Copyright (c) 2008-2009, University of Bristol
 * Copyright (c) 2008-2009, University of Manchester
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3) Neither the names of the University of Bristol and the
 *    University of Manchester nor the names of their
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import net.crew_vre.events.dao.RefinementDao;
import net.crew_vre.events.domain.facet.CountItem;
import net.crew_vre.events.domain.facet.Refinement;
import net.crew_vre.events.domain.facet.impl.CountItemImpl;
import net.crew_vre.events.domain.facet.impl.RefinementImpl;
import org.caboto.jena.db.Data;
import org.caboto.jena.db.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>A DAO implementation that gets information related to facets.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id$
 */
public class RefinementDaoImpl implements RefinementDao {

    /**
     * <p>Constructor.</p>
     */
    public RefinementDaoImpl() {
        sparqlFindNames = Utils.loadSparql("/sparql/facet-find-names.rq");
        sparqlFindName = Utils.loadSparql("/sparql/facet-find-name.rq");
        sparqlFindProperties = Utils.loadSparql("/sparql/facet-find-properties.rq");
        sparqlFindParents = Utils.loadSparql("/sparql/facet-find-parents.rq");
    }

    /**
     * <p>Find the names of refinements - used by the hierarchical facet.</p>
     *
     * @param widerProperty the property used to widen searches.
     * @param subjectUri    the resource to start the search.
     * @return a list of refinements.
     */
    public List<Refinement> findNames(String widerProperty, String subjectUri, final Data data) {

        // hold the refinements
        List<Refinement> names = new ArrayList<Refinement>();

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("widerProperty",
                ModelFactory.createDefaultModel().createResource(widerProperty));
        initialBindings.add("subjectUri",
                ModelFactory.createDefaultModel().createResource(subjectUri));

        QueryExecution qe = QueryExecutionFactory.create(sparqlFindNames, data.getDataset(),
                initialBindings);
        ResultSet rs = qe.execSelect();

        // execute ...
        while (rs.hasNext()) {
            names.add(getName(rs.nextSolution()));
        }

        return names;
    }

    /**
     * <p>Helper method - finds the id, title and label.</p>
     *
     * @param qs a query solution
     * @return a Refinement object.
     */
    private Refinement getName(QuerySolution qs) {

        String id = null, title = null, label = null;

        if (qs.getResource("id") != null) {
            id = qs.getResource("id").getURI();
        }

        if (qs.getLiteral("title") != null) {
            title = qs.getLiteral("title").getLexicalForm();
        }

        if (qs.getLiteral("rdfslabel") != null) {
            label = qs.getLiteral("rdfslabel").getLexicalForm();
        }

        return new RefinementImpl(id, title, label);
    }

    /**
     * <p>Find the name of a resource.</p>
     *
     * @param uri the URI of the resource.
     * @return a Refinement with a name.
     */
    public Refinement getName(String uri, final Data data) {

        Refinement refinement = null;

        // create initial bindings and execute...
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(uri));

        QueryExecution qe = QueryExecutionFactory.create(sparqlFindName, data.getDataset(),
                initialBindings);
        ResultSet rs = qe.execSelect();

        // there should only be one ...
        if (rs.hasNext()) {
            refinement = getName(rs.nextSolution());
        }

        return refinement;
    }

    /**
     * <p>Returns a list of refinenments based on a single linking property - it is used by the
     * Flat Facet.</p>
     *
     * @param linkProperty the linking property.
     * @return a list of refinements.
     */
    public List<Refinement> findProperties(String linkProperty, final Data data) {

        // hold the refinements;
        List<Refinement> refinements = new ArrayList<Refinement>();

        // has to hold unique resources
        Set<String> properties = new HashSet<String>();

        // create bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("linkProperty",
                ModelFactory.createDefaultModel().createResource(linkProperty));

        QueryExecution qe = QueryExecutionFactory.create(sparqlFindProperties, data.getDataset(),
                initialBindings);
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {
            QuerySolution qs = rs.nextSolution();
            if (qs.getResource("$object") != null) {
                properties.add(qs.getResource("$object").getURI());
            }
        }

        // for each property - find the name
        for (String uri : properties) {
            refinements.add(getName(uri, data));
        }

        return refinements;
    }

    /**
     * <p>Find the parents of a resource - used in the hierachical facets.</p>
     *
     * @param linkProperty the linking property.
     * @param childUri     the child URI we want to find the parents.
     * @return the parent of the childUri or null.
     */
    public Refinement findParents(String linkProperty, String childUri, final Data data) {

        // create the bindings
        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("childUri",
                ModelFactory.createDefaultModel().createResource(childUri));
        initialBindings.add("linkProperty",
                ModelFactory.createDefaultModel().createResource(linkProperty));
        // execute

        QueryExecution qe = QueryExecutionFactory.create(sparqlFindParents, data.getDataset(),
                initialBindings);
        ResultSet rs = qe.execSelect();


        Refinement refinement = null;

        // there should only be one ...
        if (rs.hasNext()) {
            refinement = getName(rs.nextSolution());
        }

        return refinement;
    }

    /**
     * <p>Count the items that are available for a refinement, e,g, how many events start with the
     * letter A. It returns a list of count objects - these objects include graph details which
     * allows us to filter the results if the person browsing isn't allowed to access an event.
     * This allows us to display a number in the UI of matching events that a person is allows to
     * view rather than all matching events.</p>
     *
     * @param sparqlFragment SPARQL fragment that constraints the count.
     * @param type           the RDF type we are interested in.
     * @return return a list of count objects.
     */
    public List<CountItem> countRefinements(String sparqlFragment, String type, String paramName, final Data data) {

        List<CountItem> countList = new ArrayList<CountItem>();

        String sparql = createSparql(sparqlFragment, type, paramName);

        //System.out.println(sparql);


        QueryExecution qe = QueryExecutionFactory.create(sparql, data.getDataset());
        if (data.getContext() != null) qe.getContext().setAll(data.getContext());
        ResultSet rs = qe.execSelect();

        while (rs.hasNext()) {

            String graph = null, id = null, value = null;

            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("graph") != null) {
                graph = qs.getResource("graph").getURI();
            }

            if (qs.getResource("id") != null) {
                id = qs.getResource("id").getURI();
            }

            if (paramName != null) {
                if (qs.getLiteral(paramName) != null) {
                    value = qs.getLiteral(paramName).getLexicalForm();
                }
            }

            countList.add(new CountItemImpl(graph, id, value));
        }

        return countList;
    }

    private String createSparql(String sparqlFragment, String type, String paramName) {

        String sparql = sparqlSelect;

        if (paramName != null) {
            sparql = new StringBuilder(sparql).append(" ?")
                    .append(paramName).append("\n").toString();
        }

        sparql = new StringBuilder(sparql).append(" WHERE { GRAPH $graph {").toString();

        if (type != null) {
            sparql = new StringBuilder().append(sparql)
                    .append("?id rdf:type ").append("<").append(type).append(">")
                    .append(" .\n").toString();
        }

        if (sparqlFragment != null) {
            sparql = new StringBuilder().append(sparql)
                    .append(sparqlFragment)
                    .append("\n").toString();
        }

        sparql += "}\n}\n";


        return sparql;
    }

    private String sparqlSelect =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                    "SELECT ?graph ?id";


    private String sparqlFindNames;
    private String sparqlFindName;
    private String sparqlFindProperties;
    private String sparqlFindParents;
}
