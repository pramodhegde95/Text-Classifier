How to run:

To run on Eclipse:
1) Load the folder "Assignment_2" directly as it is on to Eclipse IDE
2) To run NBClassifer
     2.1 Go to run configuration and in th arguments give "yes" or "no"
     2.2 yes: to remove the stopword
     2.3 no: not to remove the stopword
I am displaying the overall accuracy. If Spam and Ham accuracy is needed just uncomment the println statement of spamaccuracy and hamaccuracy

3) To run LBClassifier
     3.1 In run configuration arguments give "yes 0.01 0.1" or "no 0.01 0.1"
     3.2 yes or no: to remove or not to remove stop words
     3.3 second argument is learning rate 
     3.4 third argument is regularization parameter 

default value for lamda:0.01 and regularization:0.1
I have considered 30 iteration: To make it less just change the "iteration" variable to what ever value you need.(It took 45 minutes to run 30 iterations)
I am displaying the overall accuracy. If Spam and Ham accuracy is needed just uncomment the println statement of spamaccuracy and hamaccuracy




Through Command prompt (open command prompt from the same folder):
1) Keep the"train" "test" folders and stopwords files all in the same file
2) To run NBClassifier: 
     2.1 javac NBClassifier.java
     2.2 java NBClassfier yes 
           yes: to remove the stop words
           no: not to remove the stop words
I am displaying the overall accuracy. If Spam and Ham accuracy is needed just uncomment the println statement of spamaccuracy and hamaccuracy

3) To run the LBClassifier
     3.1 javac LBClassifier.java
     3.2 java LBClassifier yes 0.01 0.1
           yes or no: to remove or not to remove stop words
           2nd argument is learning rate
           3rd argument is regularization parameter

default value for lamda:0.01 and regularization:0.1
I have considered 30 iteration: To make it less just change the "iteration" variable to what ever value you need.(It took 45 minutes to run 30 iterations)
I am displaying the overall accuracy. If Spam and Ham accuracy is needed just uncomment the println statement of spamaccuracy and hamaccuracy



726ce120-e6f9-45e9-93bf-131d846749b6