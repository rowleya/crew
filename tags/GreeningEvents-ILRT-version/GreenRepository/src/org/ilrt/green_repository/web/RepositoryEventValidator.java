/**
 * Copyright (c) 2010, University of Bristol
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

package org.ilrt.green_repository.web;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.ilrt.green_repository.RepositoryEventManagementFacade;
import org.apache.oro.text.perl.Perl5Util;

/**
 *
 * @author Phil Cross (phil.cross@bristol.ac.uk)
 */
public class RepositoryEventValidator implements Validator {

    private RepositoryEventManagementFacade facade;
    private static final String DATE_REGEXP = "/\\d{4}-\\d{2}-\\d{2}/";

    public RepositoryEventValidator(RepositoryEventManagementFacade facade) {
        this.facade = facade;
    }

    public boolean supports(Class clazz) {
        return clazz.equals(RepositoryEventForm.class);
    }

    public void validate(Object command, Errors errors) {

        RepositoryEventForm form = (RepositoryEventForm) command;

        // only validate if the cancel button is *NOT* selected

        if (form.getCancelButton() == null) {

            // error form, field name, error code
            // Fields that shouldn't be empty
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "repository.title.missing");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "repository.location.missing");

            // check the protocol
            if (!(form.getEventUrl().startsWith("http://")
                    || form.getEventUrl().startsWith("https://"))) {
                errors.rejectValue("eventUrl", "repository.eventUrl.protocol");
            }

            // check date formats are yyyy-MM-dd
            Perl5Util perl5Util = new Perl5Util();
            if (!perl5Util.match(DATE_REGEXP, form.getStartDate())) {
                errors.rejectValue("startDate", "repository.startDate.format");
            }
            if (!perl5Util.match(DATE_REGEXP, form.getEndDate())) {
                errors.rejectValue("endDate", "repository.endDate.format");
            }

            // check enddate is same as or after startdate
            if (form.getStartDateObj() != null
                    && form.getEndDateObj() != null
                    && form.getStartDateObj().compareTo(form.getEndDateObj()) > 0) {
                errors.rejectValue("endDate", "repository.endDate.notafter");
            }

        }
    }

    // Methods for page validation
    public void validateTitle(Object command, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "repository.title.missing");
    }

    public void validateDates(Object command, Errors errors) {
        RepositoryEventForm form = (RepositoryEventForm) command;

            // check date formats are yyyy-MM-dd
            Perl5Util perl5Util = new Perl5Util();
            if (!perl5Util.match(DATE_REGEXP, form.getStartDate())) {
                errors.rejectValue("startDate", "repository.startDate.format");
            }
            if (!perl5Util.match(DATE_REGEXP, form.getEndDate())) {
                errors.rejectValue("endDate", "repository.endDate.format");
            }

            // check enddate is same as or after startdate
            if (form.getStartDateObj() != null
                    && form.getEndDateObj() != null
                    && form.getStartDateObj().compareTo(form.getEndDateObj()) > 0) {
                errors.rejectValue("endDate", "repository.endDate.notafter");
            }
    }

    public void validateUrl(Object command, Errors errors) {
        RepositoryEventForm form = (RepositoryEventForm) command;
        
        // Not mandatory so check if there is an eventUrl value first
        if (form.getEventUrl() != null && !form.getEventUrl().equals("")) {
            if (!(form.getEventUrl().startsWith("http://")
                    || form.getEventUrl().startsWith("https://"))) {
                errors.rejectValue("eventUrl", "repository.eventUrl.protocol");
            }
        }
    }

    public void validateLocation(Object command, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "repository.location.missing");
    }

}