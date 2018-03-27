import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class NBClassifier {

	public TreeMap<String, Integer> spamMap;  //words count in spam folder
	public TreeMap<String, Integer> HamMap;	//words count in ham folder
	public HashSet<String> uniqueWords;    //set to have only unique words
	public HashSet<String> stopwords; //file with all the stopwords from the provided link
	public HashMap<String, Double> spamProb; 
	public HashMap<String, Double> hamProb;

	public NBClassifier() {//constructor
		// TODO Auto-generated constructor stub
		uniqueWords = new HashSet<>();
		spamMap  = new TreeMap<>();
		HamMap = new TreeMap<>();
		stopwords = new HashSet<>();
		spamProb = new HashMap<>();
		hamProb = new HashMap<>();
	}
	
	public int spam= 0;
	public int ham = 0;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		//read all the data from the files
		File SpamTrainFile = new File("train/spam");
		File HamTrainFile = new File("train/ham");
		File SpamTestFile = new File("test/spam");
		File HamTestFile = new File("test/ham");
		File stopWordFile = new File("stopwords.txt");
		
		String nextLine = null;

		NBClassifier classify = new NBClassifier();
		
		BufferedReader reader = new BufferedReader(new FileReader(stopWordFile));
		
		while ((nextLine = reader.readLine()) != null) {
				String stopWord =nextLine;
				classify.stopwords.add(stopWord);	
		}

		//read the command line argument
		String remove_stopwords = null;
		remove_stopwords = args[0];	

		classify.createVocabulary(SpamTrainFile);
		classify.createVocabulary(HamTrainFile);

		//removing stop words if specified
		if (remove_stopwords.equals("yes")) {
			System.out.println("Stop Words removed");
			classify.removeStopWords(classify);
		}

		classify.countVocabularyOfFolder(SpamTrainFile,classify.spamMap);
		classify.countVocabularyOfFolder(HamTrainFile, classify.HamMap);

		classify.train();

		// calculation of log priors to avoid underflow
		double priorProbSpam = classify.calcultePriorSpam(SpamTrainFile,HamTrainFile);//just the length of files
		double priorProbHam =  Math.log(1 - priorProbSpam);
		
		//number of files 
		int total = SpamTestFile.listFiles().length;
		int totalHam =HamTestFile.listFiles().length;

		//calculation of accuracy
		double spam_accuracy=classify.spamAccuracy(SpamTestFile,classify,priorProbHam,priorProbSpam,remove_stopwords,classify.stopwords);
	    double finalspamaccuracy=spam_accuracy/(double)total;
		//System.out.println("Spam Accuracy : " + finalspamaccuracy * 100 + " %");
		double ham_accuracy =classify.hamAccuracy(HamTestFile,classify,priorProbHam,priorProbSpam,remove_stopwords,classify.stopwords);
		double finalhamaccuracy=ham_accuracy/(double)totalHam;
		//System.out.println("Ham Accuracy : " + finalhamaccuracy * 100 + " %");
		
		double total_accuracy=((spam_accuracy + ham_accuracy) / (totalHam + total)) * 100;
		System.out.println("Overall Accuracy  : " + total_accuracy + " %");
		//System.out.println(uniqueWords.size());
}



	private void countVocabularyOfFolder(File a, TreeMap<String, Integer> tMap) throws IOException {
		// TODO Auto-generated method stub
		String nextLine=null;
		for (File file : a.listFiles()) {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((nextLine = reader.readLine()) != null) {
				String line=nextLine;
					for (String word : line.toLowerCase().trim().split(" ")) {
						if (!word.isEmpty()) {
							if (uniqueWords.contains(word)) {
								if (tMap.containsKey(word)) {
									tMap.put(word, tMap.get(word) + 1);
								} else {
									tMap.put(word, 1);
								}
							}
						}
					}
				}
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

	private void removeStopWords(NBClassifier classify) {
		for (String str : classify.stopwords) {
			str = str.trim().toLowerCase();
			if (classify.uniqueWords.contains(str)) {
				classify.uniqueWords.remove(str);
			}
		}
		
	}
	
	private double calcultePriorSpam(File a,File b) {	
		 return(Math.log(a.listFiles().length / (double) (a.listFiles().length + b.listFiles().length)));	
	}
	
	private double spamAccuracy(File a,NBClassifier n,double s,double h,String s1,HashSet<String> hs) throws IOException {
		double result = 0;
		for (File file : a.listFiles()) {
			if (n.test(file, s, h,s1,hs) == 0) {
				result++;
			}
		}
		return(result);
	}
	
	private double hamAccuracy(File a,NBClassifier n,double s,double h,String s1,HashSet<String> hs) throws IOException {
		double result = 0;
		for (File file : a.listFiles()) {
			if (n.test(file, s, h, s1, hs) == 1) {
				result++;
			}
		}
		return(result);
	}
	
	
	private void train() {
		// TODO Auto-generated method stub
		int spamTotalWords = 0;
		Set<?> entrySet = spamMap.entrySet();
		Iterator<?> it = entrySet.iterator();//iterate through the map an count total spam words
		while(it.hasNext()){
		    @SuppressWarnings("unchecked")
			Map.Entry<String,Integer> me = (Map.Entry<String, Integer>)it.next();
			spamTotalWords += me.getValue();
		}
		
		int hamTotalWords = 0;
		Set<?> entrySet1 = HamMap.entrySet();

		Iterator<?> it1 = entrySet1.iterator();
		while(it1.hasNext()){
		    @SuppressWarnings("unchecked")
			Map.Entry<String,Integer> me = (Map.Entry<String, Integer>)it1.next();
			hamTotalWords += me.getValue();
		}

		
		for (String word : uniqueWords) {
			if (spamMap.containsKey(word)) {
				double spamlikelihood = calculateLogLikelihood(spamMap,word,spamTotalWords,uniqueWords); 
				spamProb.put(word, spamlikelihood);
			}
		}
		
		for (String word : uniqueWords) {
			if (HamMap.containsKey(word)) {
				double hamlikelihood = calculateLogLikelihood(HamMap,word,hamTotalWords,uniqueWords);
				hamProb.put(word, hamlikelihood);
			}
		}
		spam = spamTotalWords;
		//System.out.println(spam);
		ham = hamTotalWords;
		//System.out.println(ham);

	}
	
	private double calculateLogLikelihood(TreeMap<String, Integer> spamMap2,String word,int i,HashSet<String> hs) {
		return Math.log(spamMap2.get(word) + 1.0) / (i + hs.size() + 1.0);////laplace smoothing is added 
	}
		
		
	private int test(File a, double PriorHamProb, double PriorSpamProb,String remove_stopWords,HashSet<String> stopWords) throws IOException {
		
		String nextLine=null;
		
		double probSpam = 0.0;
		double probHam = 0.0;
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(a));
		while ((nextLine = reader.readLine()) != null) {
			String line=nextLine;
			for (String text : line.trim().toLowerCase().split(" ")) {
				if (remove_stopWords.equals("yes")) {//read the cmd line input
					if(!stopWords.contains(text)) {
							if (spamProb.containsKey(text)) {
								probSpam += spamProb.get(text);
							} else {
								probSpam += Math.log(1.0 / (spam + uniqueWords.size() + 1.0));//laplace smoothing
							}
							if (hamProb.containsKey(text)) {
								probHam += hamProb.get(text);
							} else {
								probHam += Math.log(1.0 / (ham + uniqueWords.size() + 1.0));
							}
					}
				}
				else {
					if (spamProb.containsKey(text)) {//directly calculate the probabiltiy
						probSpam += spamProb.get(text);
					} else {
						probSpam += Math.log(1.0 / (spam + uniqueWords.size() + 1.0));//laplace smoothing
					}
					if (hamProb.containsKey(text)) {
						probHam += hamProb.get(text);
					} else {
						probHam += Math.log(1.0 / (ham + uniqueWords.size() + 1.0));
					}					
				}
			}
		}
		probSpam += PriorSpamProb;
		probHam += PriorHamProb;

		if (probSpam > probHam) {
			return 0;
		}
		else {
			return 1;
		}

	}
}
