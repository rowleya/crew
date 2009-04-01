package net.crew_vre.events.dao.impl;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import net.crew_vre.events.Utility;
import net.crew_vre.events.dao.EventDao;
import net.crew_vre.events.domain.Event;
import net.crew_vre.events.domain.Place;
import net.crew_vre.events.domain.Subject;
import org.apache.log4j.Logger;
import org.caboto.jena.db.Database;
import org.caboto.jena.db.Results;
import org.caboto.jena.db.Utils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is an implementation of <code>EventDao</code> that uses the Jena
 * Sematic Web Framework. SPARQL is used for querying the underlying RDF model.</p>
 * <p/>
 * <p>The DAO will execute a SPAQRL query to get the basic event details such as title and
 * dates. It will then execute a number of SPARQL queries to discover relationships that have a
 * 1 to many relationship with the event, including the subjects and tags.</p>
 *
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EventDaoImpl.java 1132 2009-03-20 19:05:47Z cmmaj $
 */
public class EventDaoImpl implements EventDao {

    /**
     * <p>Constructor used for dependency injection.</p>
     *
     * @param database the database the query
     */
    public EventDaoImpl(final Database database) {
        this.database = database;

        sparqlEvents = Utils.loadSparql("/sparql/event-details.rq");
        sparqlEventParts = Utils.loadSparql("/sparql/event-details-haspart.rq");
        sparqlEventPartOf = Utils.loadSparql("/sparql/event-details-partof.rq");
        sparqlEventSubjects = Utils.loadSparql("/sparql/event-details-subjects.rq");
        sparqlEventTags = Utils.loadSparql("/sparql/event-details-tags.rq");
        sparqlEventPlaces = Utils.loadSparql("/sparql/event-details-places.rq");
    }


    /**
     * <p>Find the details for a specific event.<p>
     *
     * @param id an identifier for an event
     * @return an <code>Event</code> object
     */
    public Event findEventById(final String id) {

        Event event = null;

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find an event:");
            logger.debug(sparqlEvents);
        }

        Results res = database.executeSelectQuery(sparqlEvents,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {

            event = new Event();

            QuerySolution qs = rs.nextSolution();

            // get the id
            Resource resource = qs.getResource("id");
            event.setId(resource.getURI());

            if (qs.getResource("graph") != null) {
                event.setGraph(qs.getResource("graph").getURI());
            }

            // get the title
            if (qs.getLiteral("title") != null) {
                event.setTitle(qs.getLiteral("title").getLexicalForm());
            }

            // get the description
            if (qs.getLiteral("description") != null) {
                event.setDescription(qs.getLiteral("description").getLexicalForm());
            }

            // get the start and end date times -> xsd:dateTime
            DateTime startDateTime = null;
            DateTime endDateTime = null;

            if (qs.getLiteral("startDateTime") != null) {
                try {
                    startDateTime = Utility.parseStringToDateTime(qs.getLiteral("startDateTime").getLexicalForm());
                    endDateTime = Utility.parseStringToDateTime(qs.getLiteral("endDateTime").getLexicalForm());
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);

            // get the start and end dates -> xsd:date (needed for Intute data)
            LocalDate startDate = null;
            LocalDate endDate = null;
/**
            if (qs.getLiteral("startDate") != null) {
                try {
                    startDate = Utility.parseStringToDateTime(qs.getLiteral("startDateTime").getLexicalForm());
                    endDate = Utility.parseStringToDateTime(qs.getLiteral("endDateTime").getLexicalForm());
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
**/

            // get the event venue (place)
            event.setPlaces(findEventPlaces(event.getId()));

            // get the programme
            if (qs.getResource("programme") != null) {
                event.setProgramme(qs.getResource("programme").getURI());
            }

            // get the proceedings
            if (qs.getResource("proceedings") != null) {
                event.setProceedings(qs.getResource("proceedings").getURI());
            }

            // get the event parts
            event.setParts(findEventParts(event.getId()));

            // find partOf relationships
            event.setPartOf(findEventsIsPartOf(event.getId()));

            // get the subject areas
            event.setSubjects(findEventSubjects(event.getId()));

            // get the tags
            event.setTags(findEventTags(event.getId()));

            // presentation stuff - initial attempt: will need refactoring
            if (qs.getLiteral("recordingId") != null) {
                event.setRecording(qs.getLiteral("recordingId").getLexicalForm());
            }
        }

        // clean up
        res.close();

        return event;
    }


    /**
     * <p>An event can have parts which are also events. For example, a track might have a number
     * of sessions. This method searches for the parts and gets the basic details, such as title
     * and dates, adds them to an <code>Event</code> object and store them in a <code>List</code>.
     * To obtain the full details for a part (i.e. an event) an application will need call to the
     * <code>findEventById</code> method.</p>
     *
     * @param id the identifier of the parent event
     * @return a list of events that are children of the parent event
     */
    private List<Event> findEventParts(final String id) {

        List<Event> results = new ArrayList<Event>();

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("eventId", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find parts for an event:");
            logger.debug(sparqlEventParts);
        }

        Results res = database.executeSelectQuery(sparqlEventParts,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {

            QuerySolution qs = rs.nextSolution();

            Event event = new Event();
            event.setId(qs.getResource("id").getURI());

            if (qs.getResource("graph") != null) {
                event.setGraph(qs.getResource("graph").getURI());
            }

            if (qs.getLiteral("name") != null) {
                event.setTitle(qs.getLiteral("name").getLexicalForm());
            }

            // get the start and end dates
            DateTime startDate = null;
            DateTime endDate = null;

            if (qs.getLiteral("startDateTime") != null) {
                try {
                    startDate = Utility.parseStringToDateTime(qs.getLiteral("startDateTime").getLexicalForm());
                    endDate = Utility.parseStringToDateTime(qs.getLiteral("endDateTime").getLexicalForm());
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            event.setStartDateTime(startDate);
            event.setEndDateTime(endDate);

            results.add(event);
        }

        res.close();

        return results;
    }


    /**
     * <p>An event might be part of another event  - this method finds the parent events.
     * Recursion is used to create a chain of parent events.</p>
     *
     * @param id the identifier of the child event
     * @return a <code>List</code> of parent events
     */
    private List<Event> findEventsIsPartOf(final String id) {

        List<Event> results = new ArrayList<Event>();

        return findPartOf(results, id);
    }


    /**
     * <p>This method is called recursively to find the events that an event is part of, i.e.
     * we can create a chain of events for navigation purposes.</p>
     *
     * @param results a List of events
     * @param id      the id of an event we want to find if it is the part of another event
     * @return a list of events
     */
    private List<Event> findPartOf(List<Event> results, final String id) {

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("eventId", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find a parent event:");
            logger.debug(sparqlEventPartOf);
        }

        Results res = database.executeSelectQuery(sparqlEventPartOf,
                initialBindings);
        ResultSet rs = res.getResults();

        // there should be just one...
        while (rs.hasNext()) {

            QuerySolution qs = rs.nextSolution();

            Event event = new Event();
            event.setId(qs.getResource("id").getURI());

            if (qs.getResource("graph") != null) {
                event.setGraph(qs.getResource("graph").getURI());
            }

            if (qs.getLiteral("name") != null) {
                event.setTitle(qs.getLiteral("name").getLexicalForm());
            }

            results.add(0, event);
            results = findPartOf(results, event.getId());
        }

        res.close();

        return results;
    }


    /**
     * <p>An event might be associated with subject areas defined in a SKOS taxonomy. This method
     * finds these subjects and adds them to a <code>List</code>.</p>
     *
     * @param id the identifier of the event
     * @return a <code>List</code> of subject areas
     */
    private List<Subject> findEventSubjects(final String id) {

        List<Subject> subjects = new ArrayList<Subject>();

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find a subject areas for an event:");
            logger.debug(sparqlEventSubjects);
        }

        Results res = database.executeSelectQuery(sparqlEventSubjects,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {

            QuerySolution qs = rs.nextSolution();
            Subject subject = new Subject();
            subject.setId(qs.getResource("subjectId").getURI());

            if (qs.getResource("graph") != null) {
                subject.setGraph(qs.getResource("graph").getURI());
            }

            if (qs.getLiteral("name") != null) {
                subject.setName(qs.getLiteral("name").getLexicalForm());
            }

            subjects.add(subject);
        }

        res.close();

        return subjects;
    }


    /**
     * <p>An event might be given folksomony tags - something less formal than an a SKOS
     * taxonomy. This method finds the tags and adds them to a <code>List</code>.</p>
     *
     * @param id the identifier of the event
     * @return a <code>List</code> of folksomony tags
     */
    private List<String> findEventTags(final String id) {

        List<String> tags = new ArrayList<String>();

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find folksomony tags:");
            logger.debug(sparqlEventTags);
        }

        Results res = database.executeSelectQuery(sparqlEventTags,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {
            QuerySolution qs = rs.nextSolution();
            tags.add(qs.getLiteral("tag").getLexicalForm());
        }

        res.close();

        return tags;
    }


    /**
     * <p>An event might be taking place in more than one location - for example, a main lecture
     * theatre and an overflow room. This method finds the basic details of those places and
     * adds them to a <code>List</code>. If an application wants the full details for a place
     * then it will need to use the <code>PlaceDao</code> object.</p>
     *
     * @param id the identifier of the event
     * @return a <code>List</code> of places
     * @see net.crew_vre.events.dao.PlaceDao
     */
    private List<Place> findEventPlaces(final String id) {

        List<Place> places = new ArrayList<Place>();

        QuerySolutionMap initialBindings = new QuerySolutionMap();
        initialBindings.add("id", ModelFactory.createDefaultModel().createResource(id));

        if (logger.isDebugEnabled()) {
            logger.debug("SPARQL used to find places used by an event:");
            logger.debug(sparqlEventPlaces);
        }

        Results res = database.executeSelectQuery(sparqlEventPlaces,
                initialBindings);
        ResultSet rs = res.getResults();

        while (rs.hasNext()) {

            Place place = new Place();

            QuerySolution qs = rs.nextSolution();

            if (qs.getResource("placeId") != null) {
                place.setId(qs.getResource("placeId").getURI());
            }

            if (qs.getResource("graph") != null) {
                place.setGraph(qs.getResource("graph").getURI());
            }

            if (qs.getLiteral("name") != null) {
                place.setTitle(qs.getLiteral("name").getLexicalForm());
            }

            places.add(place);
        }

        res.close();

        return places;
    }


    // sparql for the event details
    private String sparqlEvents;

    // sparql to find events that have an "eswc:hasPart" relationship
    private String sparqlEventParts;

    // sparql to find events that have an "eswc:isPartOf" relationship
    private String sparqlEventPartOf;

    // sparql to find subjects for an event
    private String sparqlEventSubjects;

    // sparql to find tags for an event
    private String sparqlEventTags;

    // sparql to find basic details of places
    private String sparqlEventPlaces;

    // logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * The database that is queried
     */
    private final Database database;
}
