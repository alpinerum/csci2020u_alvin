import java.io.*;
import java.util.*;

public class WordCounter {
    private Map<String,Integer> wordCounts;
    private Map<String,Integer> trainHamFreq;
    private Map<String,Integer> trainSpamFreq;
    private Map<String, Float> probHamMap;
    private Map<String, Float> probSpamMap;
    private Map<String, Float> probSpam;
    private List<String> wordList = new ArrayList<String>();
    private int spamFileCount = 0;
    private int hamFileCount = 0;
    private boolean isSpam = false;

    public WordCounter() {
        wordCounts = new TreeMap<>();
        trainHamFreq = new TreeMap<>();
        trainSpamFreq = new TreeMap<>();
        probHamMap = new TreeMap<>();
        probSpamMap = new TreeMap<>();
        probSpam = new TreeMap<>();
    }

    public void processFile(File file, String outFile) throws IOException {
        System.out.println("Processing " + file.getAbsolutePath() + "...");
        if (file.isDirectory()) {
            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current: contents) {
                processFile(current, outFile);
            }
        } else if (file.exists()) {
            // count the words in this file
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s");//"[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                // checks if string is a word
                if (isWord(word)) {
                    if (file.getParent().toLowerCase().contains("spam")) {
                        isSpam = true;
                        if (wordList.contains(word.toLowerCase())) {
                            ;
                        }
                        else {
                            countSpam(word.toLowerCase());
                            wordList.add(word.toLowerCase());
                        }
                    }
                    else {
                        //if the word is in the list, counter won't increment
                        //this means the counter will count how many files contain at least one instance of that word
                        if (wordList.contains(word.toLowerCase())) {
                            ;
                        }
                        else {
                            countWord(word.toLowerCase());
                            wordList.add(word.toLowerCase());
                        }
                    }
                }
                if(isSpam) {
                    spamFileCount++;
                }
                else {
                    hamFileCount++;
                }
            }
            //clears the list for next file
            wordList.clear();
        }
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        if (word.matches(pattern)) {
            return true;
        } else {
            return false;
        }

        // also fine:
        //return word.matches(pattern);
    }

    private void countWord(String word) {

//        if (wordCounts.containsKey(word)) {
//            int oldCount = wordCounts.get(word);
//            wordCounts.put(word, oldCount+1);
//        }
        if (trainHamFreq.containsKey(word)){
            int oldCount = trainHamFreq.get(word);
            trainHamFreq.put(word, oldCount+1);
        }
        else {
            //int oldCount = trainHamFreq.get(word);
            wordCounts.put(word, 1);
            trainHamFreq.put(word, 1);
        }
    }

    private void countSpam(String word) {

        if (trainSpamFreq.containsKey(word)){
            int oldCount = trainSpamFreq.get(word);
            trainSpamFreq.put(word, oldCount+1);
        }
        else {
            //int oldCount = trainHamFreq.get(word);
            wordCounts.put(word, 1);
            trainSpamFreq.put(word, 1);
        }
    }

    public void outputWordCounts(int minCount, File outFile)
            throws IOException {
        //System.out.println("Saving word counts to " + outFile.getAbsolutePath());
        //System.out.println("# of words: " + wordCounts.keySet().size());
        System.out.println("Saving word counts to " + outFile.getAbsolutePath());
        System.out.println("# of files with word: " + probHamMap.keySet().size());
        if (!outFile.exists()) {
            outFile.createNewFile();
            if (outFile.canWrite()) {
                PrintWriter fileOut = new PrintWriter(outFile);

                Set<String> keys = probHamMap.keySet();
                Iterator<String> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    float count = probHamMap.get(key);

                    if (count >= minCount) {
                        fileOut.println(key + ": " + count);
                    }
                }

                fileOut.close();
            } else {
                System.err.println("Error:  Cannot write to file: " + outFile.getAbsolutePath());
            }
        } else {
            System.err.println("Error:  File already exists: " + outFile.getAbsolutePath());
            System.out.println("outFile.exists(): " + outFile.exists());
            System.out.println("outFile.canWrite(): " + outFile.canWrite());
        }
    }

    public void outputWordCountsSpam(int minCount, File outFile)
            throws IOException {
        //System.out.println("Saving word counts to " + outFile.getAbsolutePath());
        //System.out.println("# of words: " + wordCounts.keySet().size());
        System.out.println("Saving word counts to " + outFile.getAbsolutePath());
        System.out.println("# of files with word: " + trainSpamFreq.keySet().size());
        if (!outFile.exists()) {
            outFile.createNewFile();
            if (outFile.canWrite()) {
                PrintWriter fileOut = new PrintWriter(outFile);

                Set<String> keys = probHamMap.keySet();
                Iterator<String> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    float count = probHamMap.get(key);

                    if (count >= minCount) {
                        fileOut.println(key + ": " + count);
                    }
                }

                fileOut.close();
            } else {
                System.err.println("Error:  Cannot write to file: " + outFile.getAbsolutePath());
            }
        } else {
            System.err.println("Error:  File already exists: " + outFile.getAbsolutePath());
            System.out.println("outFile.exists(): " + outFile.exists());
            System.out.println("outFile.canWrite(): " + outFile.canWrite());
        }
    }

    public void probWS() {
//        Set<String> keys = trainSpamFreq.keySet();
//        Iterator<String> keyIterator = keys.iterator();
//
//        while (keyIterator.hasNext()) {
//            String key = keyIterator.next();
//            int count = trainSpamFreq.get(key);
//            float prob = float(count)/spamFileCount;
//            probSpamMap.put(key, prob);
//        }
        //loops over entire treemap to calculate probability of words appearing in spam emails
        for (Map.Entry<String, Integer> entry : trainSpamFreq.entrySet()) {
            String word = entry.getKey();
            int wordCount = entry.getValue();
            float prob = (float)wordCount/spamFileCount;
            //populates probability map with words as keys and probabilities as values
            probSpamMap.put(word, prob);
        }
    }

    public void probWH() {
        //loops over entire treemap to calculate probability of words appearing in ham emails
        for (Map.Entry<String, Integer> entry : trainHamFreq.entrySet()) {
            String word = entry.getKey();
            int wordCount = entry.getValue();
            float prob = (float)wordCount/hamFileCount;
            //populates probability map with words as keys and probabilities as values
            probHamMap.put(word, prob);
        }
    }

    public void probSW() {
        for (Map.Entry<String, Float> entry : probSpamMap.entrySet()) {
            String word = entry.getKey();
            if (probHamMap.containsKey(word)) {
                float prob = probSpamMap.get(word) / (probSpamMap.get(word) + probHamMap.get(word));
            }
            else {
                float prob = 1.0f;
            }
        }
    }
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java WordCounter <dir> <outfile>");
            System.exit(0);
        }

        WordCounter wordCounter = new WordCounter();
        File dataDir = new File(args[0]);
        File outFile = new File(args[1]);

        try {
            wordCounter.processFile(dataDir, args[1]);
            wordCounter.probWH();
            wordCounter.probWS();
            wordCounter.probSW();
            if (args[1].toLowerCase().contains("spam")) {
                wordCounter.outputWordCountsSpam(2, outFile);
            }
            else {
                wordCounter.outputWordCounts(2, outFile);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}