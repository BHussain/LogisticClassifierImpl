package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.Iris;
import model.IrisType;
import weka.classifiers.functions.Logistic;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Test class for solving the iris classification problem.
 * Mainly used to learn about the Weka library and logistic classification.
 * 
 * @author Basit
 *
 */
public class Main {
	/** Logger*/
	private static Logger LOG = Logger.getLogger("Client");
	/** File containing the training data*/
	private static final String TRAINING_FILE = "trainingData.txt";
	/** File containing the test data, these do not have class values*/
	private static final String TEST_FILE = "testDataNoClass.txt";
	/** File containing the test data, these have the proper classes assigned*/
	private static final String ANSWER_FILE = "testData.txt";
	/**
	 * Create an Instances object to hold the input data.
	 * Used to demonstrate the process of converting POJO(Plain Old Java Objects) to instances.
	 * The classifier object requires these in order to function.
	 * */ 
	
	/** Returns an empty Instances object for the Iris dataset. */
	private Instances buildDataSet(){
		/** Create the header information for the dataset, requires ArrayList<Attribute> attInfo. */
		ArrayList<Attribute> attInfo = new ArrayList<>();
		
		/** Create the attributes. */
		Attribute sepalLength = new Attribute("sepal_length");
		Attribute sepalWidth = new Attribute("sepal_width");
		Attribute length = new Attribute("length");
		Attribute width = new Attribute("width");
		
		List<String> types = new ArrayList<>();
		
		for(IrisType type: IrisType.values()){
			types.add(type.name());
		}
		
		Attribute type = new Attribute("type",types);
		
		attInfo.add(sepalLength);
		attInfo.add(sepalWidth);
		attInfo.add(length);
		attInfo.add(width);
		attInfo.add(type);
		Instances result = new Instances("Iris",attInfo,0);
		result.setClassIndex(result.numAttributes() - 1);
		return result;
	}
	
	/** Retrieves the training set and populates an Instance object with it */
	public Instances populateTraningSet(){
		Instances result = buildDataSet();
		System.out.println(result);
		List<Iris> toConvert = loadIrisData(TRAINING_FILE);
		for(Iris iris:toConvert){
			/** Build the attribute value vector*/
			Instance instance = new DenseInstance(5);
			instance.setDataset(result);
			instance.setValue(0, iris.getSepalLength());
			instance.setValue(1, iris.getSepalWidth());
			instance.setValue(2, iris.getLength());
			instance.setValue(3, iris.getWidth());
			instance.setValue(4, iris.getType().name());
			result.add(instance);
		}
		return result;
	}
	
	/** Method used to read the iris.data file and convert the contents to Iris objects. */
	private List<Iris> loadIrisData(String file){
		BufferedReader inputStream = null;
	    String splitter = ",";
	    String line;
		ArrayList<Iris> result = new ArrayList<>();
		
		try {
            inputStream = new BufferedReader(new FileReader(file));
            while ((line = inputStream.readLine()) != null) {
                String[] data = line.split(splitter);
                if(data.length>4){
                	Iris iris = new Iris(
                    		Double.parseDouble(data[0]),
                    		Double.parseDouble(data[1]),
                    		Double.parseDouble(data[2]),
                    		Double.parseDouble(data[3]),
                    		data[4]
                    		);
                    result.add(iris);
                }else{
                	Iris iris = new Iris(
                			Double.parseDouble(data[0]),
                    		Double.parseDouble(data[1]),
                    		Double.parseDouble(data[2]),
                    		Double.parseDouble(data[3]),
                    		"");
                	result.add(iris);
                }
                
            }
        } catch (NumberFormatException | IOException e) {
			LOG.severe("[Main] an error has occured: ");
			e.printStackTrace();
		} finally {
            if (inputStream != null) {
                try {
					inputStream.close();
				} catch (IOException e) {
					LOG.severe("[Main] an error has occured: ");
					e.printStackTrace();
				}
            }
        }
		return result;
	}
	
	/** Prepares the test data to be classified. */
	public List<Instance> prepareTestData(){
		List<Instance> result = new ArrayList<>();
		List<Iris> flowers = loadIrisData(TEST_FILE);
		for(Iris flower:flowers){
			Instance instance = new DenseInstance(0.0,new double[]{
				flower.getSepalLength(),
				flower.getSepalWidth(),
				flower.getLength(),
				flower.getWidth()
			});
			result.add(instance);
		}
		return result;
	}
	
	/** Load the test data with the correct assigned classes. Return a list representing the correct class strings*/
	public List<String> getAnswers(){
		List<String> answers = new ArrayList<>();
		List<Iris> flowers = loadIrisData(ANSWER_FILE);
		for(Iris iris:flowers){
			switch(iris.getType().name()){
			case "IRIS_SETOSA":
				answers.add("Iris-setosa");
				break;
			case "IRIS_VERSICOLOUR":
				answers.add("Iris-versicolor");
				break;
			case "IRIS_VIRGINICA":
				answers.add("Iris-virginica");
				break;
			}
		}
		return answers;
	}
	
	public String evaluate(Logistic classifier){
		String result="";
		List<Instance> testData = prepareTestData();
		List<String> answers = getAnswers();
		List<Integer> failedIndexes = new ArrayList<>();
		for(int i=0;i<testData.size();i++){
			try {
				double[] probs = classifier.distributionForInstance(testData.get(i));
				if(!getClass(probs).equals(answers.get(i))){
					failedIndexes.add(i);
				}
			} catch (Exception e) {
				LOG.warning("Failed to classify instance: "+"\n"+testData.get(i));
				failedIndexes.add(i);
				e.printStackTrace();
			}
			
		}
		result = "Evaluated "+testData.size()+" instances."+"\n"
				+"Succesrate: " +(100-((failedIndexes.size()/testData.size()*100)))+"%"+"\n"
				+"The failed indexes are: ";
		for(Integer index:failedIndexes){
			result= result+index+", ";
		}
		return result;
	}
	
	/** Given an array of doubles return a string representing the most likely class*/
	public String getClass(double[] probs){
		String result ="";
		double largest = probs[0];
	    int largestIndex = 0;

	    for(int i = 0; i < probs.length; i++)
	    {
	        if(probs[i] > largest) {
	            largest = probs[i]; 
	            largestIndex =i;
	        }  
	    }
	    
	    switch(largestIndex){
	    case 0:
	    	result = "Iris-setosa";
	    	break;
	    case 1:
	    	result = "Iris-versicolor";
	    	break;
	    case 2:
	    	result = "Iris-virginica";
	    	break;
	    }

		return result;
	}
	
	
	
	public static void  main(String[] args){
		Main client = new Main();
		/** Logistic classifier.*/
		Logistic test = new Logistic();
		/** Retrieve input data*/
		Instances instances = client.populateTraningSet();
		System.out.println("This is the training set that was loaded: ");
		System.out.println(instances);
		
		/** Train the classifier.*/
		try {
			System.out.println("Proceeding to train the classifier----------------");
			test.buildClassifier(instances);
		} catch (Exception e) {
			LOG.severe("[Main] an error has occured: ");
			e.printStackTrace();
		}
		
		System.out.println(test.toString());
		
		/** Use it to classify the validation set.*/
		System.out.println(client.evaluate(test));
		/*
		Instance setosa = new DenseInstance(0.0,new double[]{4.9,3.1,1.5,0.2});
		Instance versicolor = new DenseInstance(0.0,new double[]{6.0,3.4,4.5,1.6});
		Instance virginica = new DenseInstance(0.0,new double[]{7.7,3.0,6.1,2.3});
		
		try {
			System.out.println("Trying to classify a new instance, should be an Iris-setosa");
			double[] result =test.distributionForInstance(setosa);
			System.out.println("The probability distribution");
			for(double value:result){
				System.out.println(value);
			}
			
			System.out.println("Trying to classify a new instance, should be an Iris-versicolor");
			double[] result2 =test.distributionForInstance(versicolor);
			System.out.println("The probability distribution");
			for(double value:result2){
				System.out.println(value);
			}
			
			System.out.println("Trying to classify a new instance, should be an Iris-virginica");
			double[] result3 =test.distributionForInstance(virginica);
			System.out.println("The probability distribution");
			for(double value:result3){
				System.out.println(value);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
