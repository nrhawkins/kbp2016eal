package edu.washington.common;


import java.util.ArrayList;
import java.util.List;

//import static com.google.common.base.Preconditions.checkArgument;


public class KBPDocument {

	private final String docId;
        private String docDate;
	private String headline;	
	private String captureDate;
	private String publicationDate;
	private String name;
        private String value;
        private String rawText;
        private String cleanXMLString;

	public KBPDocument(String docId) {

        //checkArgument(docId!=null && docId.trim().length() > 0);
        //checkArgument(docType!=null && docType.trim().length() > 0);
        //checkArgument(language!=null && language.trim().length() > 0);
        this.docId = docId.trim();
        
	}

        public void setValue(String value) {
            //checkArgument(value!=null && value.trim().length()>0);
            this.value = value;
        }

	public String getDocId() {
	    return docId;
	}

        public String getDocDate() {
            return docDate;
        }

        public void setDocDate(String docDate) {
            this.docDate = docDate;
        }
  
	public String getHeadline() {
	    return headline;
	}

	public void setHeadline(String headline) {
            this.headline = headline;
	}
	
	public String getCaptureDate() {
            return captureDate;
	}

	public void setCaptureDate(String captureDate) {
	    this.captureDate = captureDate;
	}
	
	public String getPublicationDate() {                
            return publicationDate;
	}

	public void setPublicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

        public String getRawText() {
	    return rawText;
        }

        public void setRawText(String rawText) {
	    this.rawText = rawText;
        }

        public String getCleanXMLString() {
	    return cleanXMLString;
        }

        public void setCleanXMLString(String textNoXML) {
	    this.cleanXMLString = textNoXML;
        }

}
