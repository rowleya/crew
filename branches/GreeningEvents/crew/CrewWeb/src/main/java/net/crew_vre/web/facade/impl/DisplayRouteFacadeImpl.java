package net.crew_vre.web.facade.impl;

import net.crew_vre.events.dao.PlaceDao;
import net.crew_vre.events.domain.Place;
import net.crew_vre.events.dao.StartPointDao;
import net.crew_vre.events.domain.StartPoint;
import net.crew_vre.web.facade.DisplayRouteFacade;

/**
 * @author: Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version: $Id: DisplayPlaceFacadeImpl.java 534 2007-12-20 17:10:53Z cmmaj $
 */
public class DisplayRouteFacadeImpl implements DisplayRouteFacade {

    public DisplayRouteFacadeImpl(PlaceDao placeDao, StartPointDao startPointDao) {
        this.placeDao = placeDao;
        this.startPointDao = startPointDao;
    }

    public Place displayPlace(String placeId) {
        return placeDao.findPlaceById(placeId);
    }

    public StartPoint displayStartPoint(String startPointId) {
        return startPointDao.findStartPointById(startPointId);
    }

    private PlaceDao placeDao;
    private StartPointDao startPointDao;
}
