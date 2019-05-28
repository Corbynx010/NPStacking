# NPStacking
1. The program reads the user input "NPStack filepath generationCount populationSizePerGeneration"

2. The program reads all the box dimentions into an ArrayList

3. An intial population of populationSize is then generated 

The initial population is created by randomly selecting boxes until there are no boxes left to attempt to add to the tower and     running the addBox method in the current tower for each box

addBox: Tries to add the box at the bottom of the tower in any orientation then if that is not posible moves up and tries           to add the box to the second to bottom position of the tower in any orientation. This itterates upwards until the box               is either fit or is unable to be fit anywhere in the tower and is discarded.


4. The tallest tower is carried over to the next generation of towers as this can drastically increase the quality of the next generation (Elitism)

5. The two tallest towers (best parents) including the one being carried over of the last population are breed together to form the next generation.

To breed the two towers boxest are selected at random from the towers and added to the child tower. Any box that is already in the child tower is discarded (This is highly likely after a great number of generations). Along side this there is a chance to mutate a new box into the tower from the original set of boxes. The mutation rate is calculated by:

4*(1.05 - x/y) = Mutation Rate

Where 

x = current generation

y = max number of generations

A dynamic mutation rate was chosen as during earlier generations a higher cover rate of all posible boxes is optimal so that the genetic algorithm doesn't focus in on a local maxima (height). While in later generations mutation can slow optimisation of the gene pool.

6. After the next generation is created the algorithm skips back to step (4) This happens as many times as the user specified generations.

7. After the final generation is created the tallest tower from the final generation is printed to console
