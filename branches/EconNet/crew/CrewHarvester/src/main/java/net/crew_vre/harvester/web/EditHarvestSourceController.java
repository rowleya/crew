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
package net.crew_vre.harvester.web;

import net.crew_vre.harvester.HarvestSource;
import net.crew_vre.harvester.HarvesterSourceManagementFacade;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Mike Jones (mike.a.jones@bristol.ac.uk)
 * @version $Id: EditHarvestSourceController.java 1190 2009-03-31 13:22:30Z cmmaj $
 */
public class EditHarvestSourceController extends AbstractHarvestSourceController {

    public EditHarvestSourceController(HarvesterSourceManagementFacade facade) {
        this.facade = facade;
    }

    @Override
    public ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
                                 BindException errors) {

        ModelAndView mav = new ModelAndView("editHarvestSource");

        HarvestSourceForm harvestSourceForm = new HarvestSourceForm();

        String id = request.getParameter("id");

        if (id != null) {
            HarvestSource source = facade.getSource(id);
            if (source != null) {
                harvestSourceForm.setLocation(source.getLocation());
                harvestSourceForm.setDescription(source.getDescription());
                harvestSourceForm.setBlocked(source.isBlocked());
                harvestSourceForm.setAuthorityList(facade.lookupPermissions(id));
                mav.addObject("source", harvestSourceForm);
                mav.addObject("authorities", facade.getAuthoritiesList(source.getLocation()));
                mav.addObject("type", "edit");
            }
        }

        return mav;
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 Object command, BindException errors) {

        HarvestSourceForm source = (HarvestSourceForm) command;

        // only do something if the update button was pressed
        if (source.getUpdateButton() != null) {

            // update the harvester source
            facade.updateSource(source.getLocation(), source.getName(), source.getDescription(),
                    source.getLastVisited(), source.getLastStatus(), source.isBlocked());

            // update the access controls
            facade.updatePermissions(source.getLocation(),
                    findAuthorities(source.getLocation(), request));
        }

        return new ModelAndView("redirect:./listHarvestSources.do");
    }

    private final HarvesterSourceManagementFacade facade;
}