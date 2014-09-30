# HMM Tagger

If reading Markdown in TXT hurts your eyes, treat yourself to

[https://github.com/linanqiu/cs4705/tree/master/hw1](https://github.com/linanqiu/cs4705/tree/master/hw1)

- `EmissionParameters.java` handles the emission parameters
- `Markov.java` handles the N-grams and HMM

## Question 4

### Part 1

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.setCounts(new File("ner.counts"));
double emission = emissionParameters.getWordProbability("Germany","I-LOC");
```

The code above gives the `P(Germany | I-LOC)`

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.setCounts(new File("ner.counts"));
double emission = emissionParameters.getMaxWordProbability("Germany");
```

The code above gives `P(Germany | Y)` where `Y` is the tag that maximizes the probability.

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.setCounts(new File("ner.counts"));
double emission = emissionParameters.getMaxWordProbabilityTag("Germany");
```

The code above gives `Y` where `Y` is the tag that maximizes the probability `P(Germany | Y)`

### Part 2

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.emissionParameters.replaceRare(new File("ner_train.dat"), new File("ner.counts"));
```

The code above replaces the rares in file `ner_train.dat` given the counts file `ner.counts`. `ner.counts` is produced using the given Python script.

It produces a file `ner_train_replaced.dat`.

### Part 3

After rare replacement is done in part 2, you will have to run the python script on `ner_train_replaced.dat` from part 2.

```
$ python count_freqs.py ner_train_replaced.dat > ner_replaced.count
```

Now that we have the new count file, we can do part 3 where we calculate log probabilities using only the emission parameter.

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.setCounts(new File("ner_replaced.count"));
emissionParameters.writeProbabilities(new File("ner_dev.dat"));
```

This will write to a file titled `ner_dev_prediction.dat`

On running

```
$ python eval_ne_tagger.py ner_dev.key ner_dev_prediction.dat
```

The following results were obtained

```
Found 14043 NEs. Expected 5931 NEs; Correct: 3117.

        precision   recall    F1-Score
Total:  0.221961    0.525544  0.312106
PER:    0.435451    0.231230  0.302061
ORG:    0.475936    0.399103  0.434146
LOC:    0.147750    0.870229  0.252612
MISC:   0.491689    0.610206  0.544574
```

*Note: When running my code, you will not get this result as I have replaced the single rare tag system with the bucketed one in Question 6. You'll probably get something higher due to the buckets.*

## Question 5

### Part 1

```java
Markov markov = new Markov();
markov.readCounts(new File("ner_replaced.count"));
double ratio = markov.trigramBigramRatio("I-ORG", "I-ORG", "O");
```

This code will produce the `log[Count(I-ORG, I-ORG, O) / Count(I-ORG, I-ORG)]` ie. the log trigram/bigram ratio

### Part 2

```java
Markov markov = new Markov();
markov.readCounts(new File("ner_replaced.count"));
markov.writeProbabilities(new File("ner_dev.dat"));
```

This code will write the viterbi log probabilities for the each word of the sentence up to that word. **It excludes stop since the description did not ask for it and TAs clarified on piazza that the stop sign was not necessary**. Including stop is trivial anyway.

This code produces a file `ner_dev_prediction.dat`.

On running

```
$ python eval_ne_tagger.py ner_dev.key ner_dev_prediction.dat
```

The following results are produced

```
Found 4657 NEs. Expected 5931 NEs; Correct: 3145.

        precision   recall    F1-Score
Total:  0.675327    0.530265  0.594069
PER:    0.538406    0.404244  0.461778
ORG:    0.539554    0.397608  0.457831
LOC:    0.850667    0.695747  0.765447
MISC:   0.750948    0.644951  0.693925
```

This is a marked improvement over the naive emission only tagger. This can be attributed to the tagger taking into account the position of the tags in the sentence and the tags that went before it.

The code works by first constructing a `piTable` that consists of values for `pi(k, u, v)` value, iteratively upwards. It then finds the highest probability tag for each `k`. Essentially, viterbi.

## Question 6

The different rare tags are already baked into `EmissionParameters` class.

The buckets used are adaptations (read: lazy) of Bickel 1999's buckets seen in the notes.

```java
public static final String RARE_TWODIGITNUM = "_TWODIGITNUM_";
public static final String RARE_FOURDIGITNUM = "_FOURDIGITNUM_";
public static final String RARE_CONTAINSDIGITANDALPHA = "_CONTAINSDIGITANDALPHA_";
public static final String RARE_CONTAINSDIGITANDDASH = "_CONTAINSDIGITANDDASH_";
public static final String RARE_CONTAINSDIGITANDSLASH = "_CONTAINSDIGITANDSLASH_";
public static final String RARE_CONTAINSDIGITANDCOMMA = "_CONTAINSDIGITANDCOMMA_";
public static final String RARE_CONTAINSDIGITANDPERIOD = "_CONTAINSDIGITANDPERIOD_";
public static final String RARE_OTHERNUM = "_OTHERNUM_";
public static final String RARE_ALLCAPS = "_ALLCAPS_";
public static final String RARE_INITCAP = "_INITCAP_";
public static final String RARE_LOWERCASE = "_LOWERCASE_";
public static final String RARE_OTHER = "_OTHER_";
```

The buckets are constructed using basic RegEx. See the method `rareWordBucket()` for more details.

Using this code,

```java
EmissionParameters emissionParameters = new EmissionParameters();
emissionParameters.replaceRare(new File("ner_train.dat"), new File("ner.counts"));
```

Then running

```
$ python count_freqs.py ner_train_replaced.dat > ner_replaced.count
```

Then finally

```java
Markov markov = new Markov();
markov.readCounts(new File("ner_replaced.count"));
markov.writeProbabilities(new File("ner_dev.dat"));
```

We have the following results

```
Found 6537 NEs. Expected 5931 NEs; Correct: 4194.

        precision   recall    F1-Score
Total:  0.641579    0.707132  0.672762
PER:    0.587156    0.905332  0.712329
ORG:    0.487143    0.509716  0.498174
LOC:    0.840264    0.694111  0.760227
MISC:   0.729695    0.624321  0.672908
```

A marked improvement, especially in recall, over the non-bucketed version. Not much speed loss either.