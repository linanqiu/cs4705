#!/bin/bash

echo "==== Compiling"
javac TaggerTest.java

echo "==== Question 4"
echo "== Estimated runtime: 2s"
start=`date +%s`
java -Xms1024m -Xmx2048m TaggerTest 4
end=`date +%s`
runtime=$((end-start))
echo "== Runtime: $runtime"
echo "== Evaluating"
python eval_tagger.py tag_dev.key tag_dev_q4.out

echo "==== Question 5"
echo "== Estimated runtime: 1min"
start=`date +%s`
java -Xms1024m -Xmx2048m TaggerTest 5
end=`date +%s`
runtime=$((end-start))
echo "== Runtime: $runtime"
echo "== v model Written to suffix_tagger.model"
echo "== Evaluating"
python eval_tagger.py tag_dev.key tag_dev_q5.out

echo "==== Question 6"
echo "== Estimated runtime: 1.5min"
start=`date +%s`
java -Xms1024m -Xmx2048m TaggerTest 6
end=`date +%s`
runtime=$((end-start))
echo "== Runtime: $runtime"
echo "== v model written to additional_tagger.model"
echo "== Evaluating"
python eval_tagger.py tag_dev.key tag_dev_q6.out

echo "==== Done"