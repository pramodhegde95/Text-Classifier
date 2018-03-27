import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class LBClassifier {

	public HashSet<String> uniqueWords;    //set to have only unique words
	public HashMap<String, Integer> spamMap;  //words count in spam folder
	public HashMap<String, Integer> HamMap;	//words count in ham folder
	public HashSet<String> stopwords; //file with all the stopwords from the provided link
	static double lambda = 0.01;
	static double learningRate = 0.1;
	public HashSet<String> hamFiles;
	public HashSet<String> spamFiles;
	public HashSet<String> allFiles;
	public static HashMap<String, Double> weightsMap = new HashMap<String, Double>();
	int iteration=30;
	
	
	public HashMap<String, HashMap<String, Integer>> SpamFileCount;
	public HashMap<String, HashMap<String, Integer>> HamFileCount;

	public LBClassifier() {
		// TODO Auto-generated constructor stub
		uniqueWords = new HashSet<>();
		stopwords = new HashSet<>();
		spamMap = new HashMap<>();
		HamMap = new HashMap<>();
		hamFiles = new HashSet<>();
		spamFiles = new HashSet<>();
		allFiles = new HashSet<>();
		SpamFileCount = new HashMap<>();
		HamFileCount = new HashMap<>();

	}

	public static void main(String[] args) throws IOException {
		
			
		//read all the data from the files
		File SpamTrainFile = new File("train/spam");
		File HamTrainFile = new File("train/ham");
		File SpamTestFile = new File("test/spam");
		File HamTestFile = new File("test/ham");
		File stopWordFile = new File("stopwords.txt");
				
		
		String remove_stopwords = null;
		remove_stopwords = args[0];	
		learningRate = Double.parseDouble(args[1]);
		lambda = Double.parseDouble(args[2]);
		
		
		LBClassifier clasiifier = new LBClassifier();

		clasiifier.createVocabulary(SpamTrainFile);//create a vocabulary
		clasiifier.createVocabulary(HamTrainFile);
		
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(stopWordFile));
		
		String nextLine=null;
		while ((nextLine = reader.readLine()) != null) {
				String stopWord =nextLine;
				clasiifier.stopwords.add(stopWord);	
		}
		
		
		//removing stop words if specified
		if (remove_stopwords.equals("yes")) {
			System.out.println("Stop Words removed");
			clasiifier.removeStopWords(clasiifier);
			}

		clasiifier.countVocabularyOfFolder(SpamTrainFile,clasiifier.SpamFileCount, clasiifier.spamMap,clasiifier.spamFiles);
		clasiifier.countVocabularyOfFolder(HamTrainFile,clasiifier.HamFileCount,clasiifier.HamMap, clasiifier.hamFiles);

		clasiifier.train();



		// spam accuracy
		int spamfiles = SpamTestFile.listFiles().length;
		int accuracyspam=calculateAccuracySpam(SpamTestFile,remove_stopwords,clasiifier);
		double spamAccuracy = ((double) accuracyspam / (double) spamfiles) * 100;
		//System.out.println("Spam Accuracy : " + spamAccuracy + " %");

		
		// ham accuracy
		int hamfiles = HamTestFile.listFiles().length;
		int accuracyham=calculateAccuracyHam(HamTestFile,remove_stopwords,clasiifier);
		double hamAccuracy = ((double) accuracyham / (double) hamfiles) * 100;
		//System.out.println("Ham Accuracy : " + hamAccuracy + " %");
		
		System.out.println("Overall Accuracy  : " + ((double)(accuracyham + accuracyspam) / (double)(hamfiles + spamfiles)) * 100);
}

	
	private void removeStopWords(LBClassifier driver) {
		for (String str : driver.stopwords) {
			str = str.trim().toLowerCase();
			if (driver.uniqueWords.contains(str)) {
				driver.uniqueWords.remove(str);
			}
		}
		
	}
	
	private static int calculateAccuracySpam(File a,String s1,LBClassifier driver) throws IOException {
		int numFiles = 0;
		for (File files : a.listFiles()) {
			HashMap<String, Integer> testMap = new HashMap<String, Integer>();
			String nextLine=null;
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(files));
			while ((nextLine = reader.readLine()) != null) {
				String line=nextLine;
					for (String word : line.trim().toLowerCase().split(" ")) {
						if (testMap.containsKey(word)) {
							testMap.put(word, testMap.get(word) + 1);
						} else {
							testMap.put(word, 1);
						}
					}
				}
			if (s1.equals("yes")) {
				for (String sword : driver.stopwords) {
					if (testMap.containsKey(sword)) {
						testMap.remove(sword);
					}
				}
			}
			if (driver.test(testMap)==false) {
				numFiles++;
			}
		}
			return numFiles;
	}
	
	
	
	private static int calculateAccuracyHam(File a,String s1,LBClassifier driver) throws IOException {
		int numFiles = 0;
		for (File files : a.listFiles()) {
			HashMap<String, Integer> testMap1 = new HashMap<String, Integer>();
			String nextLine=null;
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(files));
			while ((nextLine = reader.readLine()) != null) {
				String line=nextLine;
					for (String word : line.trim().toLowerCase().split(" ")) {
						if (testMap1.containsKey(word)) {
							testMap1.put(word, testMap1.get(word) + 1);
						} else {
							testMap1.put(word, 1);
						}
					}
				}
			if (s1.equals("yes")) {
				for (String sword : driver.stopwords) {
					if (testMap1.containsKey(sword)) {
						testMap1.remove(sword);
					}
				}
			}
			if (driver.test(testMap1)==true) {
				numFiles++;
			}
		}
			return numFiles;
	}
	
	private void countVocabularyOfFolder(File trainFile, HashMap<String, HashMap<String, Integer>> fileMap,HashMap<String, Integer> wordMap, HashSet<String> fileSet) throws IOException {

		for (File file : trainFile.listFiles()) {
			Map<String, Integer> fileVocab = new HashMap<String, Integer>();

			fileSet.add(file.getName());
			allFiles.add(file.getName());
			String nextLine=null;
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((nextLine = reader.readLine()) != null) {
				String line=nextLine;
					for (String word : line.trim().toLowerCase().split(" ")) {
						// only add to the vocabulary if exists in the distinct
						// vocabulary set
						if (!word.isEmpty()) {
							if (uniqueWords.contains(word)) {

								if (wordMap.containsKey(word)) {
									wordMap.put(word, wordMap.get(word) + 1);
								} else {
									wordMap.put(word, 1);
								}

								if (fileVocab.containsKey(word)) {
									fileVocab.put(word, fileVocab.get(word) + 1);
								} else {
									fileVocab.put(word, 1);
								}
							}
						}

					}
				}
			fileMap.put(file.getName(), (HashMap<String, Integer>) fileVocab);
		}

	}
	
	private void createVocabulary(File a) throws IOException {
		// TODO Auto-generated method stub
		String nextLine=null;
		for (File file : a.listFiles()) {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((nextLine = reader.readLine()) != null) {
				String line=nextLine;
					for (String word : line.trim().toLowerCase().split(" ")) {	
						if (!word.isEmpty()) {
							uniqueWords.add(word);
						}
					}
				}
		}
  }
	

	private double classify(String fileName) {
		// TODO Auto-generated method stub
		double predicted = 0.0;
		if(hamFiles.contains(fileName)) {
			for(Entry<String, Integer> wordMap : HamFileCount.get(fileName).entrySet()) {
				predicted += weightsMap.get(wordMap.getKey()) * wordMap.getValue();
			}
			return sigmoid(predicted);
		}
		else {
			for(Entry<String, Integer> wordMap : SpamFileCount.get(fileName).entrySet()) {
				predicted += weightsMap.get(wordMap.getKey()) * wordMap.getValue();
			}
			return sigmoid(predicted);
		}
	}
	
	
	private double sigmoid(double predicted) {
		// TODO Auto-generated method stub
		////check overflow 
		if(predicted>20){
			return 1.0;
		}
		else if(predicted<-20){

			return 0.0;
		}
		else{
			return (1.0 /(1.0+ Math.exp(-predicted)));
		}
	}


public void train() {
		
	for (String word : uniqueWords) {
		weightsMap.put(word, 0.5);
	}
		for (int i = 0; i < iteration; i++) {
			System.out.println("Phase "+(i+1));
			for(String word : uniqueWords) {
				double weightedError = 0;
				for(String fileName : allFiles) {
					int fileClass=0;
					int countOfWord =0;
					//count the number of words in the file
					if(hamFiles.contains(fileName)) {
						fileClass = 1;
						for(Entry<String, Integer> wordSet : HamFileCount.get(fileName).entrySet()) {
							if(wordSet.getKey().equals(word)) {
								countOfWord=wordSet.getValue();
								
							}
						}
					}
					else if(spamFiles.contains(fileName)) {
     					fileClass = 0;
						for(Entry<String, Integer> wordSet : SpamFileCount.get(fileName).entrySet()) {
							if(wordSet.getKey().equals(word)) {
								countOfWord=wordSet.getValue();
							}
						}
					}
					double calculatedClass = classify(fileName);
					weightedError += countOfWord * (fileClass - calculatedClass);
				}
				//calculate new weight of the word
				double newWeight = updateWeight(word,weightedError);
				weightsMap.put(word, newWeight);
			}
		}
	}

private double updateWeight(String s,double w) {
	return(weightsMap.get(s) + learningRate * w - (learningRate * lambda * weightsMap.get(s)));
}

public boolean test(HashMap<String, Integer> testMap) {
	// TODO Auto-generated method stub
	double predicted = 0.0;
	for(Entry<String, Integer> val :testMap.entrySet()){
		if(weightsMap.containsKey(val.getKey())){
			predicted += (weightsMap.get(val.getKey())* val.getValue());
		}
	}
	if(sigmoid(predicted)>=0.5){
		return true;
	}
	else{
		return false;
	}
}

}
