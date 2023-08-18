// NLP Programming Assignment #3
// NaiveBayes
// 2012

//
// Things for you to implement are marked with TODO!
// Generally, you should not need to touch things *not* marked TODO
//
// Remember that when you submit your code, it is not run from the command line 
// and your main() will *not* be run. To be safest, restrict your changes to
// addExample() and classify() and anything you further invoke from there.
//
import java.util.*;
import java.io.*;

public class NaiveBayes {

    public static boolean FILTER_STOP_WORDS = false; // this gets set in main()
    private static List<String> stopList = readFile(new File("../data/english.stop"));
    private HashMap<String, Integer> docsByClass;     //docs numbers by class
    private HashSet<String> vocabulary;               //the vocabulary set for all docs
    private HashMap<String, Integer> wordsInClass;    //the number of each word in each class
    private HashMap<String, Integer> wordsClassCount; //number of words in each class
    private HashMap<String, Double> classProb;       //log probability of each class
    private HashMap<String, Double> wordsProb;       //log prob of each word on wordsClassCount
    private boolean isInitialize;
    
    private int numDocs;

    public NaiveBayes() {
        numDocs = 0;
        docsByClass = new HashMap<String, Integer>();
        vocabulary = new HashSet<String>();
        wordsInClass = new HashMap<String, Integer>();
        wordsClassCount = new HashMap<String, Integer>();
        classProb = new HashMap<String, Double>();
        wordsProb = new HashMap<String, Double>();
        isInitialize = false;
    }
    //TODO

    /**
     * Put your code for adding information to your NB classifier here
     **/
    public void addExample(String klass, List<String> words) {
        HashSet<String> vocDoc = new HashSet<String>();
        //count docs in class
        if (!docsByClass.containsKey(klass)) {
            docsByClass.put(klass, 0);
        }
        
        docsByClass.put(klass, docsByClass.get(klass) + 1);
        //count all documents
        numDocs++;
        
        //iterate over each word in document
        for(String word : words){
            //filter stop words
            if(FILTER_STOP_WORDS && stopList.contains(word))
                continue;
            //add word to vocabulary set
            vocabulary.add(word);
            if(vocDoc.contains(word))
                continue;
            vocDoc.add(word);

            String wc = word + " in "+ klass;
            if(!wordsInClass.containsKey(wc))
                wordsInClass.put(wc, 0);
            wordsInClass.put(wc, wordsInClass.get(wc)+1);
            
            //count total words in each class
            if(!wordsClassCount.containsKey(klass))
                wordsClassCount.put(klass, 0);
            wordsClassCount.put(klass, wordsClassCount.get(klass)+1);
        }
    }

    //TODO
    /**
     *  Put your code here for deciding the class of the input file.
     *  Currently, it just randomly chooses "pos" or "negative"
     */
    public String classify(List<String> words) {
        String bestClass=null;
        double argmax = -1;
        
        if(!isInitialize)
            initializeProbabilities();
        
        //calculate condicional prob of documents in each class
        Set<String> keys = docsByClass.keySet();
        for( String klass : keys ){
            double pdc = probDocInClass(klass, words);
            
            if(bestClass==null || pdc>argmax){
                argmax = pdc;
                bestClass = klass;
            }
        }
       
        return bestClass;
    }
    
    public void initializeProbabilities(){
        Integer count, Vc;  
        String kl;
        
        //calculate prob of class                
        for(String klass : docsByClass.keySet() ){
            count = docsByClass.get(klass);
            classProb.put(klass,  Math.log(count) - Math.log(numDocs));
        }
        
        //calculate prob of each word
        for(String wc : wordsInClass.keySet()){
            count = wordsInClass.get(wc);
            kl = wc.substring(wc.lastIndexOf(" ")+1);
            Vc = wordsClassCount.get(kl);
            
            wordsProb.put(wc, Math.log((count==null?0:count)+1) - Math.log( Vc + vocabulary.size()));
        }
        
        isInitialize = true;
    }
    
    public double probDocInClass(String klass, List<String> doc){
        Integer Vc = wordsClassCount.get(klass);
        Double pc, pwc;
        double score=0.0;
        String wc;
        
        pc = classProb.get(klass);
        score += (pc==null)?0:pc;
        for(String w: doc){
            //filter stop words
            if(FILTER_STOP_WORDS && stopList.contains(w))
                continue;
            wc = w + " in " + klass;
            pwc = wordsProb.get(wc);
            
            if(pwc==null)
                pwc = Math.log(1) - Math.log( Vc + vocabulary.size());
            score += pwc;
        }
        
        return score;
     }

    public void train(String trainPath) {
        File trainDir = new File(trainPath);
        if (!trainDir.isDirectory()) {
            System.err.println("[ERROR]\tinvalid training directory specified.  ");
        }

        TrainSplit split = new TrainSplit();
        for (File dir : trainDir.listFiles()) {
            if (!dir.getName().startsWith(".")) {
                List<File> dirList = Arrays.asList(dir.listFiles());
                for (File f : dirList) {
                    split.train.add(f);
                }
            }
        }
        for (File file : split.train) {
            String klass = file.getParentFile().getName();
            List<String> words = readFile(file);
            if (FILTER_STOP_WORDS) {
                words = filterStopWords(words);
            }
            addExample(klass, words);
        }
        return;
    }

    public List<List<String>> readTest(String ch_aux) {
        List<List<String>> data = new ArrayList<List<String>>();
        String[] docs = ch_aux.split("###");
        TrainSplit split = new TrainSplit();
        for (String doc : docs) {
            List<String> words = segmentWords(doc);
            if (FILTER_STOP_WORDS) {
                words = filterStopWords(words);
            }
            data.add(words);
        }
        return data;
    }

    /**
     * This class holds the list of train and test files for a given CV fold
     * constructed in getFolds()
     **/
    public static class TrainSplit {
        // training files for this split

        List<File> train = new ArrayList<File>();
        // test files for this split;
        List<File> test = new ArrayList<File>();
    }
    public static int numFolds = 10;

    /**
     * This creates train/test splits for each of the numFold folds.
     **/
    static public List<TrainSplit> getFolds(List<File> files) {
        List<TrainSplit> splits = new ArrayList<TrainSplit>();

        for (Integer fold = 0; fold < numFolds; fold++) {
            TrainSplit split = new TrainSplit();
            for (File file : files) {
                if (file.getName().subSequence(2, 3).equals(fold.toString())) {
                    split.test.add(file);
                } else {
                    split.train.add(file);
                }
            }

            splits.add(split);
        }
        return splits;
    }

    // returns accuracy 
    public double evaluate(TrainSplit split) {
        int numCorrect = 0;
        for (File file : split.test) {
            String klass = file.getParentFile().getName();
            List<String> words = readFile(file);
            if (FILTER_STOP_WORDS) {
                words = filterStopWords(words);
            }
            String guess = classify(words);
            if (klass.equals(guess)) {
                numCorrect++;
            }
        }
        return ((double) numCorrect) / split.test.size();
    }

    /**
     * Remove any stop words or punctuation from a list of words.
     **/
    public static List<String> filterStopWords(List<String> words) {
        List<String> filtered = new ArrayList<String>();
        for (String word : words) {
            if (!stopList.contains(word) && !word.matches(".*\\W+.*")) {
                filtered.add(word);
            }
        }
        return filtered;
    }

    /** 
     * Code for reading a file.  you probably don't want to modify anything here, 
     * unless you don't like the way we segment files.
     **/
    private static List<String> readFile(File f) {
        try {
            StringBuilder contents = new StringBuilder();

            BufferedReader input = new BufferedReader(new FileReader(f));
            for (String line = input.readLine(); line != null; line = input.readLine()) {
                contents.append(line);
                contents.append("\n");
            }
            input.close();

            return segmentWords(contents.toString());

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Splits lines on whitespace for file reading
     **/
    private static List<String> segmentWords(String s) {
        List<String> ret = new ArrayList<String>();

        for (String word : s.split("\\s")) {
            if (word.length() > 0) {
                ret.add(word);
            }
        }
        return ret;
    }

    public List<TrainSplit> getTrainSplits(String trainPath) {
        File trainDir = new File(trainPath);
        if (!trainDir.isDirectory()) {
            System.err.println("[ERROR]\tinvalid training directory specified.  ");
        }
        List<TrainSplit> splits = new ArrayList<TrainSplit>();
        List<File> files = new ArrayList<File>();
        for (File dir : trainDir.listFiles()) {
            if (!dir.getName().startsWith(".")) {
                List<File> dirList = Arrays.asList(dir.listFiles());
                for (File f : dirList) {
                    files.add(f);
                }
            }
        }
        splits = getFolds(files);
        return splits;
    }

    /**
     * build splits according to command line args.  If args.length==1
     * do 10-fold cross validation, if args.length==2 create one TrainSplit
     * with all files from the train_dir and all files from the test_dir
     */
    private static List<TrainSplit> buildSplits(List<String> args) {
        File trainDir = new File(args.get(0));
        if (!trainDir.isDirectory()) {
            System.err.println("[ERROR]\tinvalid training directory specified.  ");
        }

        List<TrainSplit> splits = new ArrayList<TrainSplit>();
        if (args.size() == 1) {
            System.out.println("[INFO]\tPerforming 10-fold cross-validation on data set:\t" + args.get(0));
            List<File> files = new ArrayList<File>();
            for (File dir : trainDir.listFiles()) {
                if (!dir.getName().startsWith(".")) {
                    List<File> dirList = Arrays.asList(dir.listFiles());
                    for (File f : dirList) {
                        files.add(f);
                    }
                }
            }
            splits = getFolds(files);
        } else if (args.size() == 2) {
            // testing/training on two different data sets is treated like a single fold
            System.out.println("[INFO]\tTraining on data set:\t" + args.get(0) + " testing on data set:\t" + args.get(1));
            TrainSplit split = new TrainSplit();
            for (File dir : trainDir.listFiles()) {
                if (!dir.getName().startsWith(".")) {
                    List<File> dirList = Arrays.asList(dir.listFiles());
                    for (File f : dirList) {
                        split.train.add(f);
                    }
                }
            }
            File testDir = new File(args.get(1));
            if (!testDir.isDirectory()) {
                System.err.println("[ERROR]\tinvalid testing directory specified.  ");
            }
            for (File dir : testDir.listFiles()) {
                if (!dir.getName().startsWith(".")) {
                    List<File> dirList = Arrays.asList(dir.listFiles());
                    for (File f : dirList) {
                        split.test.add(f);
                    }
                }
            }
            splits.add(split);
        }
        return splits;
    }

    public void train(TrainSplit split) {
        for (File file : split.train) {
            String klass = file.getParentFile().getName();
            List<String> words = readFile(file);
            if (FILTER_STOP_WORDS) {
                words = filterStopWords(words);
            }
            addExample(klass, words);
        }
    }

    public static void main(String[] args) {
        List<String> otherArgs = Arrays.asList(args);
        if (args.length > 0 && args[0].equals("-f")) {
            FILTER_STOP_WORDS = true;
            otherArgs = otherArgs.subList(1, otherArgs.size());
        }
        if (otherArgs.size() < 1 || otherArgs.size() > 2) {
            System.out.println("[ERROR]\tInvalid number of arguments");
            System.out.println("\tUsage: java -cp [-f] trainDir [testDir]");
            System.out.println("\tWith -f flag implements stop word removal.");
            System.out.println("\tIf testDir is omitted, 10-fold cross validation is used for evaluation");
            return;
        }
        System.out.println("[INFO]\tFILTER_STOP_WORDS=" + FILTER_STOP_WORDS);

        List<TrainSplit> splits = buildSplits(otherArgs);
        double avgAccuracy = 0.0;
        int fold = 0;
        for (TrainSplit split : splits) {
            NaiveBayes classifier = new NaiveBayes();
            double accuracy = 0.0;

            for (File file : split.train) {
                String klass = file.getParentFile().getName();
                List<String> words = readFile(file);
                if (FILTER_STOP_WORDS) {
                    words = filterStopWords(words);
                }
                classifier.addExample(klass, words);
            }

            for (File file : split.test) {
                String klass = file.getParentFile().getName();
                List<String> words = readFile(file);
                if (FILTER_STOP_WORDS) {
                    words = filterStopWords(words);
                }
                String guess = classifier.classify(words);
                if (klass.equals(guess)) {
                    accuracy++;
                }
            }
            accuracy = accuracy / split.test.size();
            avgAccuracy += accuracy;
            System.out.println("[INFO]\tFold " + fold + " Accuracy: " + accuracy);
            fold += 1;
        }
        avgAccuracy = avgAccuracy / numFolds;
        System.out.println("[INFO]\tAccuracy: " + avgAccuracy);
    }
}
