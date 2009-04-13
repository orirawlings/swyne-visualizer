package edu.iit.swyne;

import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.http.HTTPRepository;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.model.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class QueryBean {

   String sesameServer = "http://swyne.homelinux.org/sesame";
   String repositoryID = "SwyneDB";
   Repository myRepository;
   TupleQueryResult working_result;


   String latitude = null;
   String longitude = null;
   String distance = null;
   String date1 = null;
   String date2 = null;
	int zoom = -1;
   boolean processError = false;
   String docv = null;

   public QueryBean () {
      String latitude = null;
      String longitude = null;
      distance = null;
      date1 = null;
      date2 = null;
      docv = null;
			zoom = -1;

      try{
         myRepository = new HTTPRepository(sesameServer, repositoryID);
         myRepository.initialize();
      } catch (OpenRDFException e){
         System.out.println("Issues: " + e);
      }
   }

   public void reset() {
      latitude = null;
      longitude = null;
      distance = null;
      date1 = null;
      date2 = null;
			zoom = -1;
      docv = null;
   }

   public void setLatitude (String lat) {
      this.latitude = lat;
   }

   public String getLatitude () {
      return this.latitude;
   }

   public void setLongitude (String lon) {
      this.longitude = lon;
   }

   public String getLongitude () {
      return this.longitude;
   }

   public void setDistance (String dis) {
      this.distance = dis;
   }

   public String getDistance () {
      return this.distance;
   }

   public void setDate1 (String dt1) {
      this.date1 = dt1;
   }

   public String getDate1 () {
      return this.date1;
   }

   public void setDate2 (String dt2) {
      this.date2 = dt2;
   }

   public String getDate2 () {
      return this.date2;
   }

   public void setDoc (String adoc) {
      this.docv = adoc;
   }

   public String getDoc () {
      return this.docv;
   }

   public void setZoom (int z) {
      this.zoom = z;
   }

   public int getZoom () {
      return this.zoom;
   }

   public Iterator processRequest (HttpServletRequest request) {
      //reset();
      LinkedList r_docs = new LinkedList();
      Iterator r_doc_iter = null;

      this.processError = false;
      //if (latitude == null || latitude.equals("")) setLatitude(request.getParameter ("latitude"));  
      //if (longitude == null || longitude.equals("")) setLongitude(request.getParameter ("longitude"));  
      //if (distance == null || distance.equals("")) setDistance(request.getParameter ("radius"));
      //if (date1 == null || date1.equals("")) setDate1(request.getParameter ("date1"));
      //if (date2 == null || date2.equals("")) setDate2(request.getParameter ("date2"));
      setLatitude(request.getParameter ("latitude"));  
      setLongitude(request.getParameter ("longitude"));  
      setDistance(request.getParameter ("radius"));
      setDate1(request.getParameter ("date1"));
      setDate2(request.getParameter ("date2"));
			setZoom(Integer.parseInt(request.getParameter("zoom")));
      if (latitude == null || longitude == null || date1 == null ||
            latitude.equals("") || longitude.equals("") || date1.equals("")) {
         this.processError = true;
      }

      try{
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection d_con = DriverManager.getConnection("jdbc:mysql://localhost/swyne", "swyneuser", "temp4now");
         java.sql.Statement stmt = d_con.createStatement();
         String query;
         if ((date1 == null) || date1.equals("") || (date2 == null) || date2.equals("")){
            query = "SELECT DISTINCT doc_id from doc_loc where( " + distance + 
               " > ACOS( SIN(RADIANS(" + latitude + ")) * SIN(RADIANS(latitude)) + " +
               "COS(RADIANS(" + latitude + ")) * COS(RADIANS(latitude)) * " +
               "COS(RADIANS(longitude - " + longitude + "))) * 6371);"; //6371 km = radius of Earth
         }
         else {
            query = "SELECT DISTINCT doc_id FROM doc_date WHERE date >= '" + formatDate(date1) + "' and date <= '" + formatDate(date2) + 
               "' AND doc_id IN (SELECT doc_id from doc_loc where( " + distance + 
               " > ACOS( SIN(RADIANS(" + latitude + ")) * SIN(RADIANS(latitude)) + " +
               "COS(RADIANS(" + latitude + ")) * COS(RADIANS(latitude)) * " +
               "COS(RADIANS(longitude - " + longitude + "))) * 6371));";
         }
         ResultSet results = stmt.executeQuery(query);
         while (results.next()) {
            String doc = results.getString("doc_id");
            //int lat = results.getInt("lat");
            //int long = results.getInt("long");
            r_docs.add(doc);
         }
         results.close();
         d_con.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }

      r_doc_iter = r_docs.iterator();
      return r_doc_iter;

   }


   public boolean getProcessError () {
      return this.processError;
   }

   public String getText(String adoc) {
      String text=null;
      try{
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection d_con = DriverManager.getConnection("jdbc:mysql://localhost/swyne_test", "swyneuser", "temp4now");
         java.sql.Statement stmt = d_con.createStatement();
         String query = "SELECT txt FROM documents WHERE doc_id = " + adoc + ";";
         ResultSet results = stmt.executeQuery(query);
         if (results.next()) {
            text = results.getString("txt");
         }
         results.close();
         d_con.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return text;
   }

   public String getCollection(String adoc) {
      String collection=null;
      try{
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection d_con = DriverManager.getConnection("jdbc:mysql://localhost/swyne_test", "swyneuser", "temp4now");
         java.sql.Statement stmt = d_con.createStatement();
         String query = "SELECT collection_name FROM collections WHERE collection IN (SELECT collection_id from documents where doc_id = " + adoc + ");";
         ResultSet results = stmt.executeQuery(query);
         if (results.next()) {
            collection = results.getString("collection_name");
         }
         results.close();
         d_con.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return collection;
   }

   public String getTitle(String adoc) {
      String title=null;
      try{
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection d_con = DriverManager.getConnection("jdbc:mysql://localhost/swyne_test", "swyneuser", "temp4now");
         java.sql.Statement stmt = d_con.createStatement();
         String query = "SELECT title FROM documents WHERE doc_id = " + adoc + ";";
         ResultSet results = stmt.executeQuery(query);
         if (results.next()) {
            title = results.getString("title");
         }
         results.close();
         d_con.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return title;
   }

   public Iterator getDates(String adoc){
      LinkedList dates = new LinkedList();
      String date=null;
      try{
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         Connection d_con = DriverManager.getConnection("jdbc:mysql://localhost/swyne_test", "swyneuser", "temp4now");
         java.sql.Statement stmt = d_con.createStatement();
         String query = "SELECT date FROM doc_date WHERE doc_id = " + adoc + ";";
         ResultSet results = stmt.executeQuery(query);
         while (results.next()) {
            date = results.getString("date");
            dates.add(date);
         }
         results.close();
         d_con.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }

      return dates.iterator();
   }

   public Iterator getLocations(String adoc){
      LinkedList entities = new LinkedList();

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?X WHERE { " + 
                  "?RESOURCE vcard:KEY ?KEY . ?RESOURCE vcard:Locality ?X . " + 
                  "FILTER (xsd:integer(?KEY) = xsd:integer(\"" + adoc + "\"))}");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("X");
                  entities.add(ent.stringValue());
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

      return entities.iterator();
   }

   public Iterator getEntities(String adoc) {
      LinkedList entities = new LinkedList();

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?X WHERE { " + 
                  "?RESOURCE vcard:KEY ?KEY . ?RESOURCE vcard:Locality ?X . " + 
                  "FILTER (xsd:integer(?KEY) = xsd:integer(\"" + adoc + "\"))}");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("X");
                  entities.add(ent.stringValue());
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?X WHERE { " + 
                  "?RESOURCE vcard:KEY ?KEY . ?RESOURCE vcard:ORG ?X . " + 
                  "FILTER (xsd:integer(?KEY) = xsd:integer(\"" + adoc + "\"))}");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("X");
                  entities.add(ent.stringValue());
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?X WHERE { " + 
                  "?RESOURCE vcard:KEY ?KEY . ?RESOURCE vcard:NAME ?X . " + 
                  "FILTER (xsd:integer(?KEY) = xsd:integer(\"" + adoc + "\"))}");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("X");
                  entities.add(ent.stringValue());
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }


      return entities.iterator();

   }
	 
	public String getType(String entity){
		String type=null;

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?pred WHERE { " + 
                  "?x ?pred \"" + entity + "\"}");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("pred");
                  //entities.add(ent.stringValue());
									type = ent.stringValue();
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

      //return entities.iterator();
      return type;
   }

	public Iterator getArticles(String entity){
      LinkedList entities = new LinkedList();

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?ent WHERE { " + 
                  "?x ?pred \"" + entity + "\" ." +
									"?x vcard:KEY ?ent }");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("ent");
                  entities.add(ent.stringValue());
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

      return entities.iterator();
   }

	public Iterator getNeighbors(String entity){
      TreeMap entities = new TreeMap();

      try {
         RepositoryConnection s_con = myRepository.getConnection();
         try {
            String queryString = ("PREFIX vcard:<http://www.w3.org/2001/vcard-rdf/3.0#> PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> SELECT ?ent WHERE { " + 
                  "?x ?pred \"" + entity + "\" ." +
									"?x ?type ?ent }");
            TupleQuery tupleQuery = s_con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            TupleQueryResult result = tupleQuery.evaluate();
            try {
               while(result.hasNext()){
                  BindingSet bindingSet = result.next();
                  Value ent = bindingSet.getValue("ent");
									String temp = ent.stringValue();
									if (entities.containsKey(temp)){
										int count = (Integer)entities.get(temp);
										count++;
										entities.put(temp, count);
									}
									else {
                  	entities.put(temp, 1);
									}
               }

            }
            finally {
               result.close();
            }
         }
         finally {
            s_con.close();
         }
      }
      catch (OpenRDFException e) {
         System.out.println("Problem connecting to sesame server");
         System.out.println(e);
      }

			Iterator e_iter = entities.keySet().iterator();
			while(e_iter.hasNext()){
				Object next = e_iter.next();
				int count = (Integer)entities.get(next);
				if (count < 2){
					e_iter.remove();
				}
			}
      return entities.keySet().iterator();
   }

   public String formatDate(String date){
      String [] split = date.split("/");
      return(split[2] + "-" + split[0] + "-" + split[1]);
   }

}
