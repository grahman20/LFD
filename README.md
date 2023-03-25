# LFD

Discretization is the process of converting numerical values into categorical values. We propose a new data-driven discretization technique called low frequency discretizer (LFD) that does not require any user input. LFD uses low frequency values as cut points and thus reduces the information loss due to discretization. It uses all other categorical attributes and any numerical attribute that has already been categorized. It considers that the influence of an attribute in discretization of another attribute depends on the strength of their relationship.

# Reference

Rahman, M. G. and Islam, M. Z. (2016): Discretization of Continuous Attributes Through Low Frequency Numerical Values and Attribute Interdependency. Expert Systems with Applications, 45, 410-423. http://dx.doi.org/10.1016/j.eswa.2015.10.005. 

## BibTeX
```
@article{rahman2016discretization,
  title={Discretization of continuous attributes through low frequency numerical values and attribute interdependency},
  author={Rahman, Md Geaur and Islam, Md Zahidul},
  journal={Expert Systems with Applications},
  volume={45},
  pages={410--423},
  year={2016},
  publisher={Elsevier}
}
```

@author Md Geaur Rahman <https://csusap.csu.edu.au/~grahman/>
  
# Two folders:
 
 1. LFD_project (NetBeans project)
 2. SampleData 
 
 LFD is developed based on Java programming language (jdk1.8.0_211) using NetBeans IDE (8.0.2). 
 
# How to run:
 
	1. Open project in NetBeans
	2. Run the project

# Sample input and output:
run:
Please enter the name of the file containing the 2 line attribute information.(example: c:\data\attrinfo.txt)

C:\SampleData\attrinfo.txt

Please enter the name of the data file having numerical values: (example: c:\data\data.txt)

C:\SampleData\data.txt

Please enter the name of the output file: (example: c:\data\out.txt)

C:\SampleData\output.txt


Data discretization by LFD is done. The completed data set is written to:  
C:\SampleData\output.txt