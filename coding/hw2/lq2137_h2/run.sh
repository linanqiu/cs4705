#!/bin/bash

echo "Q4: Starting"
echo "Q4: Counting original training data to cfg.counts"

python count_cfg_freq.py parse_train.dat > cfg.counts

echo "Q4: Replacing rares."

java -jar pcfg.jar replace cfg.counts parse_train.dat parse_train_replaced.dat

echo "Q4: Done replacing. Running counting script on parse_train_replaced.dat"

python count_cfg_freq.py parse_train_replaced.dat > cfg_replaced.counts

echo "Q4: Done counting. Output to count_replaced.counts"
echo "Q4: End"

echo "Q5: Starting"
echo "Q5: Running CKY algorithm on parse_dev.dat using cfg_replaced.counts"

start=`date +%s`
java -jar pcfg.jar cky cfg_replaced.counts parse_dev.dat parse_dev_predict.dat
end=`date +%s`
runtime=$((end-start))

echo "Q5: Runtime: $runtime"

echo "Q5: Prediction output to parse_dev_predict.dat"
echo "Q5: Running python eval script"

python eval_parser.py parse_dev.key parse_dev_predict.dat

echo "Q5: End"

echo "Q6: Starting"
echo "Q6: Counting original vert training data to cfg_vert.counts"

python count_cfg_freq.py parse_train_vert.dat > cfg_vert.counts

echo "Q6: Replacing rares."

java -jar pcfg.jar replace cfg_vert.counts parse_train_vert.dat parse_train_vert_replaced.dat

echo "Q6: Done replacing. Running counting script on parse_train_vert_replaced.dat"

python count_cfg_freq.py parse_train_vert_replaced.dat > cfg_vert_replaced.counts

echo "Q6: Done counting. Output to count_vert_replaced.counts"
echo "Q6: Running CKY algorithm on parse_dev.dat using cfg_vert_replaced.counts"

start=`date +%s`
java -jar pcfg.jar cky cfg_vert_replaced.counts parse_dev.dat parse_dev_vert_predict.dat
end=`date +%s`
runtime=$((end-start))

echo "Q6: Runtime: $runtime"

echo "Q6: Prediction output to parse_dev_vert_predict.dat"
echo "Q6: Running python eval script"

python eval_parser.py parse_dev.key parse_dev_vert_predict.dat

echo "Q6: End"



