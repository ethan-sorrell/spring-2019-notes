Usage:
To find the class for each query:
python3 main.py num_centroids queryset.txt dataset.txt
To find the class of each document:
python3 main.py num_centroids dataset.txt

Since a implementation optimized for speed would surely use many functions built-into numpy and scipy which would defeat the purposes of this assignment,
I have opted instead to focus on interpretability and introspection. All vectors are implemented as dictionaries in a {word: count} format.
I have termed this structure ds within my code. As a result, centroids can be viewed in terms of the words that are most common. Additional information of this sort could easily be gleaned by running this program in an IDE, or slightly modifying the file to print such information.

The centroids are initialized to the first k-documents.
As a result, consecutive runs should provide the same results.

When run with 5 classes:
class 0:
physics of flying

class 1:
flow theory

class 2:
shear flow
outliers (only 2 documents)

class 3:
heat transfer, shockwaves

class 4:
general class (centroid probably closest to the origin)
structural issues
