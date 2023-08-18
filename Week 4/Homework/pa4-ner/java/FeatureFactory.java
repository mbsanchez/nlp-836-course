import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class FeatureFactory {

    /** Add any necessary initialization steps for your features here.
     *  Using this constructor is optional. Depending on your
     *  features, you may not need to intialize anything.
     */
    Set<String> stopWords;
    Set<String> treatment;
    Set<String> names;
    
    public FeatureFactory() {
        stopWords = new HashSet<String>(Arrays.asList("sunday","monday","tuesday","wednesday","thursday","friday","saturday",
                                                      "january","february","march","april","may","june","july","august","september","octuber","november","december"));
        treatment = new HashSet<String>(Arrays.asList("accountant", "actor", "actress", "administrator", "agent", "aide", "ambassador", "architect", "archaeologist", "athlete", "attorney", "baker", "barber", "bartender", "beautician", "biologist", "businessman", "butcher", "captain", "carpenter", "chief", "clown", "pharmacist", "coach", "dancer", "dentist", "detective", "doctor", "electrician", "engineer", "farmer", "firefighter", "fisherman", "florist", "gardener", "geologist", "guard", "gynecologist", "jeweler", "lawyer", "librarian", "locksmith", "mechanic", "minister", "musician", "nurse", "painter", "photographer", "pilot", "poet", "policeman", "politician", "president", "professor", "psychiatrist", "psychoanalyst", "psychologist", "receptionist", "sailor", "secretary", "singer", "soldier", "student", "surgeon", "teacher", "therapist", "veterinarian", "waiter", "waitress", "welder"));
        names = new HashSet<String>(Arrays.asList("aaron", "adam", "adlai", "adrian", "agatha", "ahmed", 
                "ahmet", "aimee", "amy", "ami", "al", "alain", "alan", "alastair", "albert", 
                "alberto", "alejandro", "alex", "alexander", "alexis", "alf", "alfred", "alison", 
                "allan", "allen", "alvin", "amanda", "amarth", "amedeo", "ami", "amigo", "amir", "amos", 
                "amy", "anatole", "anatoly", "anderson", "andre", "andrea", "andreas", "andrew", "andries", 
                "andy", "angela", "angus", "anita", "ann", "anna", "annard", "anne", "annie", 
                "anthony", "anton", "antonella", "antonio", "antony", "archie", "ariel", "arlene", 
                "arne", "arnold", "art", "arthur", "audrey", "avery", "axel", "barbara", "barbra", "barney", 
                "barrett", "barrio", "barry", "bart", "barton", "bea", "becky", "beckie", "belinda", "ben", 
                "benjamin", "benson", "bernard", "bernie", "bert", "bertrand", "beth", "betsy", "betty", 
                "beverly", "bill", "billy", "billie", "bjorne", "blaine", "blair", "blake", "blayne", "bob", 
                "bobbie", "bobby", "bonnie", "boyce", "boyd", "brad", "bradford", "bradley", "brandi", "brandon", 
                "brandy", "brenda", "brendan", "brender", "brent", "bret", "brett", "brian", "briggs", "brodie", 
                "brooke", "bruce", "bruno", "bryan", "bryce", "bucky", "bud", "butler", "byron", "caleb", 
                "calvin", "carisa", "carl", "carlo", "carlos", "carol", "carole", "caroline", "carolyn", 
                "carsten", "kirsten", "cristi", "kristi", "carter", "cary", "case", "casey", "leith", "casper", 
                "cathy", "catherine", "cathrin", "cathryn", "cecilia", "celeste", "celia", "charleen", "charlene",
                "charles", "charley", "charlie", "chet", "chip", "chris", "christian", "christie", "christina",
                "christofer", "christophe", "christopher", "chuck", "charles", "cindie", "cindy", "clara", "clare",
                "claire", "clarence", "clarissa", "clark", "claude", "claudia", "claudio", "clay", "clayton", 
                "samuel", "cliff", "clifford", "clyde", "cole", "clem", "coleen", "colin", "collin", "connie", 
                "conrad", "corey", "cory", "courtney", "craig", "cris", "kris", "cristina", "cristopher", "curt", 
                "curtis", "cynthia", "cyrus", "dale", "dalton", "damon", "damone", "ramon", "dan", "dana", "dani",
                "daniel", "daniele", "danielle", "danny", "dannie", "darci", "daren", "darin", "darrell", "darren",
                "darryl", "daryl", "dave", "david", "dawn", "dawson", "dean", "deb", "debbie", "debi", "deborah", 
                "deirdre", "del", "delbert", "denis", "dennis", "derek", "devon", "huey", "dewey", "louis", 
                "louie", "diana", "diane", "dick", "richard", "dieter", "dimitry", "dimetry", "dion", "dirk", 
                "dominic", "dominick", "don", "donal", "donald", "donn", "donne", "donna", "donnie", "donovan", 
                "dori", "dory", "dorian", "dorothy", "doug", "douglas", "doyle", "drew", "duane", "duke", "duncan",
                "dustin", "dwayne", "dwight", "dylan", "earl", "earle", "earnie", "ernie", "ed", "eddy", "edgar", 
                "eddie", "edith", "edmond", "edmund", "eduardo", "edward", "edwin", "eva", "eileen", "erick", 
                "erik", "eric", "elaine", "eli", "elias", "elijah", "eliot", "elisabeth", "elizabeth", "ellen", 
                "elliot", "elliott", "elric", "elsa", "elvis", "emil", "emily", "elwood", "emma", "emmett", 
                "eric", "erik", "ernest", "ernie", "ernst", "erwin", "ethan", "eugene", "evan", "evelyn", 
                "everett", "farouk", "fay", "frederick", "felix", "fletcher", "floria", "florian", "floyd", 
                "frances", "francis", "francisco", "francois", "frank", "franklin", "jerrie", "jerry", "fred", 
                "frederic", "frederick", "fritz", "gabriel", "gail", "gale", "galen", "gary", "gene", "geoff", 
                "geoffrey", "jeff", "jeffrey", "jeffie", "george", "gerald", "jerald", "hazel", "gerard", 
                "gideon", "gigi", "gil", "gill", "gilles", "giles", "ginny", "jinny", "giovanni", "glen",
                "glenn", "glynn", "gordon", "grace", "graeme", "graham", "grant", "granville", "greg", "gregg",
                "gregge", "gregor", "gregory", "gretchen", "griff", "guido", "guillermo", "gunnar", "gunter", 
                "guy", "gypsy", "hal", "hamilton", "hank", "hans", "harmon", "harold", "harris", "harry", 
                "hartmann", "harv", "harvey", "heather", "hector", "heidi", "hein", "heinrich", "heinz", "helen",
                "helge", "henry", "herb", "herbert", "herman", "herve", "hienz", "hilda", "hillary", "hillel", 
                "himawan", "hirofumi", "hirotoshi", "hiroyuki", "hitoshi", "hohn", "holly", "hon", "honzo", 
                "horst", "hotta", "howard", "hsi", "hsuan", "huashi", "hubert", "hugh", "hughes", "hui", "hume",
                "hunter", "hurf", "hwa", "hy", "ian", "ilya", "ima", "indra", "ira", "irfan", "irvin", "irving",
                "irwin", "isaac", "isabelle", "isidore", "israel", "izchak", "izumi", "izzy", "jack", "jackye", 
                "jacob", "jacobson", "jacques", "jagath", "jaime", "jakob", "james", "jamie", "jan", "jane",
                "janet", "janice", "janos", "jared", "jarl", "jarmo", "jarvis", "jason", "jay", "jayant", "jayesh",
                "jean", "jean-christophe", "jean-pierre", "jeanette", "jeanne", "jeannette", "jeannie", "jeany",
                "jef", "jeff", "jeffery", "jeffrey", "jelske", "jem", "jenine", "jennie", "jennifer", "jeremy", 
                "jerome", "jerry", "jesper", "jess", "jesse", "jesus", "ji", "jianyun", "jill", "jim", "jimmy",
                "jin", "jinchao", "jingbai", "jiri", "jisheng", "jitendra", "joachim", "joanne", "jochen", "jock",                
                "joe", "joel", "johan", "johann", "john", "johnathan", "johnnie", "johnny", "jon", "jonathan", 
                "jones", "jong", "joni", "joon", "jordan", "jorge", "jos", "jose", "joseph", "josh", "joshua", 
                "josip", "joubert", "joyce", "juan", "judge", "judith", "judy", "juergen", "juha", "julia", 
                "julian", "juliane", "julianto", "julie", "juliet", "julius", "jun", "june", "jurevis", "juri", 
                "jussi", "justin", "jwahar", "kaj", "kamel", "kamiya", "kanthan", "karen", "kari", "karl", "kate",
                "kathleen", "kathryn", "kathy", "kay", "kayvan", "kazuhiro", "kee", "kees", "keith", "kelly", 
                "kelvin", "kemal", "ken", "kenn", "kenneth", "kent", "kenton", "kerri", "kerry", "kevan",
                "kevin", "kevyn", "kieran", "kiki", "kikki", "kim", "kimberly", "kimmo", "kinch", "king",
                "kirk", "kit", "kitty", "klaudia", "klaus", "knapper", "knudsen", "knut", "knute", "kolkka",
                "konrad", "konstantinos", "kory", "kris", "kristen", "kristi", "kristian", "kristin", "kriton",
                "krzysztof", "kuldip", "kurt", "kusum", "kyle", "kylo", "kyu", "kyung", "lana", "lance", "lanny",
                "lar", "larry", "lars", "laura", "laurel", "laurence", "laurent", "laurianne", "laurie", 
                "lawrence", "lea", "leads", "lee", "leif", "leigh", "leila", "len", "lenora", "lenny", "leo", 
                "leon", "leonard", "leora", "les", "leslie", "lester", "leung", "lewis", "lex", "liber", 
                "lievaart", "lila", "lin", "linda", "linder", "lindsay", "lindsey", "linley", "lisa", "list",
                "liyuan", "liz", "liza", "lloyd", "lois", "lonhyn", "lord", "loren", "lorenzo", "lori", "lorien",
                "lorraine", "lou", "louiqa", "louis", "louise", "loukas", "lowell", "loyd", "luc", "lucifer", 
                "lucius", "lui", "luis", "lukas", "luke", "lum", "lyndon", "lynn", "lynne", "lynnette", "maarten", 
                "mac", "magnus", "mah", "mahesh", "mahmoud", "major", "malaclypse", "malcolm", "malloy", "malus",
                "manavendra", "manjeri", "mann", "manny", "manolis", "manuel", "mara", "marc", "marcel", "marci",
                "marcia", "marco", "marcos", "marek", "margaret", "margie", "margot", "marguerite", "maria", 
                "marian", "marie", "marilyn", "mario", "marion", "mariou", "mark", "markus", "marla", "marlena",
                "marnix", "marsh", "marsha", "marshall", "martha", "martin", "marty", "martyn", "marvin", "mary", 
                "masanao", "masanobu", "mason", "mat", "mats", "matt", "matthew", "matthias", "matthieu", "matti",
                "maureen", "maurice", "max", "mayo", "mechael", "meehan", "meeks", "mehrdad", "melinda", "merat",
                "merril", "merton", "metin", "micah", "michael", "micheal", "michel", "michelle", "michiel", 
                "mick", "mickey", "micky", "miek", "mikael", "mike", "mikey", "miki", "miles", "milner", "milo",
                "miltos", "miriam", "miriamne", "mitch", "mitchell", "moe", "mohammad", "molly", "mongo", "monica",
                "monty", "moore", "moran", "morgan", "morris", "morton", "moses", "mosur", "mott", "murat", 
                "murph", "murray", "murthy", "mwa", "myrick", "myron", "mysore", "nadeem", "naim", "nancy", 
                "nanda", "naomi", "naoto", "naren", "narendra", "naresh", "nate", "nathan", "nathaniel", 
                "natraj", "neal", "ned", "neil", "nelken", "neville", "nguyen", "nhan", "niall", "nichael", 
                "nicholas", "nici", "nick", "nicolas", "nicolette", "nicolo", "niels", "nigel", "nikolai", 
                "nils", "ning", "ninja", "no", "noam", "noemi", "nora", "norbert", "norm", "norma", "norman", 
                "nou", "novo", "novorolsky", "ofer", "olaf", "old", "ole", "oleg", "oliver", "olivier", "olof",
                "olson", "omar", "orville", "oscar", "oskar", "owen", "ozan", "pablo", "page", "pam", "pamela", 
                "panacea", "pandora", "panos", "pantelis", "panzer", "paola", "part", "pascal", "pat", "patrice",
                "patricia", "patricio", "patrick", "patty", "paul", "paula", "pedro", "peggy", "penny", "per", 
                "perry", "pete", "peter", "petr", "phil", "philip", "philippe", "phill", "phillip", "phiroze",
                "pia", "piercarlo", "pierce", "pierette", "pierre", "piet", "piete", "pieter", "pilar", "pilot",
                "pim", "ping", "piotr", "pitawas", "plastic", "po", "polly", "pontus", "pradeep", "prakash", 
                "pratap", "pratapwant", "pratt", "pravin", "presley", "pria", "price", "raanan", "rabin", 
                "radek", "rafael", "rafik", "raghu", "ragnar", "rahul", "raif", "rainer", "raj", "raja", 
                "rajarshi", "rajeev", "rajendra", "rajesh", "rajiv", "rakhal", "ralf", "ralph", "ram", 
                "ramadoss", "raman", "ramanan", "ramesh", "ramiro", "ramneek", "ramsey", "rand", "randal", 
                "randall", "randell", "randolph", "randy", "ranjit", "raphael", "rathnakumar", "raul", "ravi", 
                "ravindran", "ravindranath", "ray", "rayan", "raymond", "real", "rebecca", "rees", "reid", 
                "reiner", "reinhard", "renu", "revised", "rex", "rhonda", "ric", "ricardo", "rich", "richard",
                "rick", "ricky", "rik", "ritalynne", "ritchey", "ro", "rob", "robbin", "robert", "roberta", 
                "roberto", "robin", "rod", "rodent", "roderick", "rodger", "rodney", "roger", "rogue", "roland",
                "rolf", "rolfe", "romain", "roman", "ron", "ronald", "ronni", "root", "ross", "roxana", "roxane",
                "roxanne", "roxie", "roy", "rudolf", "rudolph", "rudy", "rupert", "russ", "russell", "rusty", 
                "ruth", "saad", "sabrina", "saify", "saiid", "sal", "sally", "sam", "samir", "samuel", "sanand", 
                "sanche", "sandeep", "sandip", "sandra", "sandy", "sanford", "sangho", "sanity", "sanjay", 
                "sanjeev", "sanjib", "santa", "saqib", "sarah", "sassan", "saul", "saumya", "scot", "scott", 
                "sean", "sedat", "sedovic", "seenu", "sehyo", "sekar", "serdar", "sergeant", "sergei", "sergio", 
                "sergiu", "seth", "seymour", "shadow", "shahid", "shai", "shakil", "shamim", "shane", "shankar", 
                "shannon", "sharada", "sharan", "shari", "sharon", "shatter", "shaw", "shawn", "shean", "sheila",
                "shel", "sherman", "sherri", "shirley", "sho", "shutoku", "shuvra", "shyam", "sid", "sidney",
                "siegurd", "sigurd", "simon", "siping", "sir", "sjaak", "sjouke", "skeeter", "skef", "skip", 
                "slartibartfast", "socorrito", "sofia", "sofoklis", "son", "sonja", "sonny", "soohong", "sorrel", 
                "space", "spass", "spencer", "spike", "spock", "spudboy", "spy", "spyros", "sri", "sridhar",
                "sridharan", "srikanth", "srinivas", "srinivasan", "sriram", "srivatsan", "ssi", "stacey", 
                "stacy", "stagger", "stan", "stanislaw", "stanley", "stanly", "starbuck", "steen", "stefan",
                "stephan", "stephanie", "stephe", "stephen", "stevan", "steve", "steven", "stewart", "straka",
                "stu", "stuart", "subra", "sue", "sugih", "sumitro", "sundar", "sundaresan", "sunil", "suresh",
                "surya", "susan", "susanne", "susumu", "suu", "suwandi", "suyog", "suzan", "suzanne", "svante",
                "swamy", "syd", "syed", "sylvan", "syun", "tad", "tahsin", "tai", "tait", "takao", "takayuki", 
                "takeuchi", "tal", "tammy", "tanaka", "tandy", "tanya", "tao", "tareq", "tarmi", "taurus", "ted",
                "teresa", "teri", "teriann", "terrance", "terrence", "terri", "terry", "teruyuki", "thad",
                "tharen", "the", "theo", "theodore", "thierry", "think", "thomas", "those", "thuan", "ti", 
                "tiefenthal", "tigger", "tim", "timo", "timothy", "tobias", "toby", "todd", "toerless", "toft",
                "tolerant", "tollefsen", "tom", "tomas", "tommy", "tony", "tor", "torsten", "toufic", "tovah",
                "tracey", "tracy", "tran", "travis", "trent", "trevor", "trey", "triantaphyllos", "tricia", 
                "troy", "trying", "tuan", "tuna", "turkeer", "tyler", "uri", "urs", "vadim", "val", "valentin",
                "valeria", "valerie", "van", "vance", "varda", "vassos", "vaughn", "venkata", "vern", "vernon",
                "vic", "vice", "vick", "vicki", "vickie", "vicky", "victor", "victoria", "vidhyanath", "vijay",
                "vilhelm", "vince", "vincent", "vincenzo", "vinod", "vishal", "vistlik", "vivek", "vladimir",
                "vladislav", "wade", "walt", "walter", "warren", "wayne", "wendell", "wendy", "wendi", "werner",
                "wes", "will", "william", "willie", "wilmer", "wilson", "win", "winnie", "winston", "wolf", 
                "wolfgang", "woody", "yvonne"));
    }

    /**
     * Words is a list of the words in the entire corpus, previousLabel is the label
     * for position-1 (or O if it's the start of a new sentence), and position
     * is the word you are adding features for. PreviousLabel must be the
     * only label that is visible to this method. 
     */
    private List<String> computeFeatures(List<String> words,
					 String previousLabel, int position) {

	List<String> features = new ArrayList<String>();

	String currentWord = words.get(position);
    String previousWord = position>0?words.get(position-1):"";
	String nextWord = position+1<words.size()?words.get(position+1):"";

	// Baseline Features  
	features.add("word=" + currentWord);
	features.add("prevLabel=" + previousLabel);
	features.add("word=" + currentWord + ", prevLabel=" + previousLabel);
        
	/** Warning: If you encounter "line search failure" error when
	 *  running the program, considering putting the baseline features
	 *  back. It occurs when the features are too sparse. Once you have
	 *  added enough features, take out the features that you don't need. 
	 */

	// TODO: Add your features here      
        //features.add("initial=0" );
        if(previousWord.toLowerCase().matches("in|on|at"))
            features.add("place"/*+currentWord+", prevWord="+previousWord*/);

        if(currentWord.matches("[A-Z]([a-z]+)") || currentWord.matches("Mc[A-Z][a-z]+|[A-Z]'[A-Z][a-z]+|[A-Z][a-z]+'s") /*||
		   (previousLabel.equals("PERSON") && currentWord.matches("[A-Z]([a-z]+)"))*/){
            features.add("realname");
        }
        
        /*if(currentWord.matches("Mc[A-Z][a-z]+|[A-Z]'[A-Z][a-z]+|[A-Z][a-z]+'s"))
            features.add("realname");*/
        
        if(names.contains(currentWord.toLowerCase()))
            features.add("realname="+currentWord.toLowerCase());
        
        /*if(previousLabel.equals("PERSON") && features.contains("realname"))
            features.add("surname");*/
			
		if(previousLabel.equals("O") && features.contains("realname"))
            features.add("othercaps");
        
        if(previousLabel.equals("O") && features.contains("realname") && nextWord.matches("[A-Z][a-z]+"))
            features.add("othername");
        
        if(stopWords.contains(currentWord.toLowerCase()))
                features.add("weekDay"/*="+currentWord*/);
            
        //if(treatment.contains(previousWord.toLowerCase()) && features.contains("realname"))
        //    features.add("treat"/*=" + currentWord + ", prevWord=" + previousWord*/);

        //if(previousWord.equalsIgnoreCase("by") && features.contains("realname"))
        //    features.add("author"/*="+currentWord+", prevWord="+previousWord*/);
        
        if(currentWord.matches(".*(,|\\.).*"))
            features.add("punctuation");
        
        if(currentWord.matches("$ist|$st|$ing"))
            features.add("other");
			
		if(currentWord.matches("\\.*[0-9]+\\.*"))
			features.add("number");
        
        
        /*if(previousWord.matches("[A-Z]([a-z]+)") && previousLabel.equals("O"))
            features.add("title="+previousLabel);*/
            
        /*else if(currentWord.matches("[A-Z]+"))
            features.add("ALLCASE");
        else if(currentWord.matches("[a-z]+"))
            features.add("lowercase");
        else
            features.add("CamelCase");*/
        
        
        
	return features;
    }


    /** Do not modify this method **/
    public List<Datum> readData(String filename) throws IOException {

	List<Datum> data = new ArrayList<Datum>();
	BufferedReader in = new BufferedReader(new FileReader(filename));

	for (String line = in.readLine(); line != null; line = in.readLine()) {
	    if (line.trim().length() == 0) {
		continue;
	    }
	    String[] bits = line.split("\\s+");
	    String word = bits[0];
	    String label = bits[1];

	    Datum datum = new Datum(word, label);
	    data.add(datum);
	}

	return data;
    }

    /** Do not modify this method **/
    public List<Datum> readTestData(String ch_aux) throws IOException {

	List<Datum> data = new ArrayList<Datum>();

	for (String line : ch_aux.split("\n")) {
	    if (line.trim().length() == 0) {
		continue;
	    }
	    String[] bits = line.split("\\s+");
	    String word = bits[0];
	    String label = bits[1];

	    Datum datum = new Datum(word, label);
	    data.add(datum);
	}

	return data;
    }

    /** Do not modify this method **/
    public List<Datum> setFeaturesTrain(List<Datum> data) {
	// this is so that the feature factory code doesn't accidentally use the
	// true label info
	List<Datum> newData = new ArrayList<Datum>();
	List<String> words = new ArrayList<String>();

	for (Datum datum : data) {
	    words.add(datum.word);
	}

	String previousLabel = "O";
	for (int i = 0; i < data.size(); i++) {
	    Datum datum = data.get(i);

	    Datum newDatum = new Datum(datum.word, datum.label);
	    newDatum.features = computeFeatures(words, previousLabel, i);
	    newDatum.previousLabel = previousLabel;
	    newData.add(newDatum);

	    previousLabel = datum.label;
	}

	return newData;
    }

    /** Do not modify this method **/
    public List<Datum> setFeaturesTest(List<Datum> data) {
	// this is so that the feature factory code doesn't accidentally use the
	// true label info
	List<Datum> newData = new ArrayList<Datum>();
	List<String> words = new ArrayList<String>();
	List<String> labels = new ArrayList<String>();
	Map<String, Integer> labelIndex = new HashMap<String, Integer>();

	for (Datum datum : data) {
	    words.add(datum.word);
	    if (labelIndex.containsKey(datum.label) == false) {
		labelIndex.put(datum.label, labels.size());
		labels.add(datum.label);
	    }
	}

	// compute features for all possible previous labels in advance for
	// Viterbi algorithm
	for (int i = 0; i < data.size(); i++) {
	    Datum datum = data.get(i);

	    if (i == 0) {
		String previousLabel = "O";
		datum.features = computeFeatures(words, previousLabel, i);

		Datum newDatum = new Datum(datum.word, datum.label);
		newDatum.features = computeFeatures(words, previousLabel, i);
		newDatum.previousLabel = previousLabel;
		newData.add(newDatum);

	    } else {
		for (String previousLabel : labels) {
		    datum.features = computeFeatures(words, previousLabel, i);

		    Datum newDatum = new Datum(datum.word, datum.label);
		    newDatum.features = computeFeatures(words, previousLabel, i);
		    newDatum.previousLabel = previousLabel;
		    newData.add(newDatum);
		}
	    }

	}

	return newData;
    }

    /** Do not modify this method **/
    public void writeData(List<Datum> data, String filename)
	throws IOException {


	FileWriter file = new FileWriter(filename + ".json", false);

	       
	for (int i = 0; i < data.size(); i++) {
	    try {
		JSONObject obj = new JSONObject();
		Datum datum = data.get(i);
		obj.put("_label", datum.label);
		obj.put("_word", base64encode(datum.word));
		obj.put("_prevLabel", datum.previousLabel);

		JSONObject featureObj = new JSONObject();

		List<String> features = datum.features;
		for (int j = 0; j < features.size(); j++) {
		    String feature = features.get(j).toString();
		    featureObj.put("_" + feature, feature);
		}
		obj.put("_features", featureObj);
		obj.write(file);
		file.append("\n");
	    } catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	file.close();
    }

    /** Do not modify this method **/
    private String base64encode(String str) {
	Base64 base = new Base64();
	byte[] strBytes = str.getBytes();
	byte[] encBytes = base.encode(strBytes);
	String encoded = new String(encBytes);
	return encoded;
    }

}
