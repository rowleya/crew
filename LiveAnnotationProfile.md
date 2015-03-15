# Introduction #

The profile.xml file defines the names and data types to be used in the Live annotations.

```
<profile id="LiveAnnotation" type="http://www.crew-vre.net/2008/10/crew-ns#LiveAnnotation" >
    <!-- type of the liveAnnotation {question|answer|comment|link|reference|important|slide|person} -->
    <profileEntry id="liveAnnotationType" 
        objectDatatype="String"
        propertyType="http://purl.org/dc/elements/1.1/type"
        required="true" />
    <!-- name of person/title of the of link/reference -->
    <profileEntry id="CrewLiveAnnotationTitle" 
        objectDatatype="String"
        propertyType="http://purl.org/dc/elements/1.1/title"
        required="false" />
    <!-- A comment/content of the annotation -->
    <profileEntry id="CrewLiveAnnotationComment" 
        objectDatatype="String"
        propertyType="http://purl.org/dc/elements/1.1/description"
        required="false" />
    <!-- A url connected to the annotation -->
    <profileEntry id="CrewLiveAnnotationUrl" 
        objectDatatype="String"
        propertyType="http://www.crew-vre.net/2008/10/crew-ns#url"
        required="false" />
    <!-- A email address connected to the annotation -->
    <profileEntry id="CrewLiveAnnotationEmail" 
        objectDatatype="String"
        propertyType="http://www.crew-vre.net/2008/10/crew-ns#email"
        required="false" />
    <!-- A email address connected to the annotation -->
    <profileEntry id="CrewLiveAnnotationRelatesTo" 
        objectDatatype="String"
        propertyType="http://www.crew-vre.net/2008/10/crew-ns#relates-to"
        required="false" />
</profile>
```