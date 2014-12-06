#!/bin/bash

echo "==== Compiling"
javac TaggerTest.java

echo "==== Running"
echo "== Estimated runtime: ~6min"
start=`date +%s`
java -Xms1024m -Xmx2048m Test
end=`date +%s`
runtime=$((end-start))
echo "== Runtime: $runtime"

echo "==== Question 4"
echo "== 4.1: No answer required"
echo "== 4.2: ibm1_devwords_ranking.txt"
echo "== 4.3: ibm1_alignment.txt"

echo "==== Question 5"
echo "== 5.1: No answer required"
echo "== 5.2: No answer required"
echo "== 5.3: ibm2_alignment.txt"

echo "==== Question 6"
echo "== 6.1 unscrambled.en"
echo "== Evaluating Question 6"

python eval_scramble.py unscrambled.en original.en

echo "==== Done"