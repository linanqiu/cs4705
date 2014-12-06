# Report for HW3

## Quick Start

To run everything, simply place

- All default files from `hw3.tar.gz`. Please unzip `corpus.de.gz` and `corpus.en.gz` as well. The directory should contain

```
corpus.de
corpus.en
alignment_sample_model1.txt
alignment_sample_model2.txt
eval_scramble.py
sample_t_model1.txt
original.de
scrambled.en
devwords.txt
original.en
```

- `run.sh`
- Java files

```
Counts.java
IBM1.java
IBM2.java
Test.java
Unscramble.java
```

into the same folder.

Then run

```
sh run.sh
```

To run all of question 4, 5, and 6, and see outputs and run time.

The program allocates **1024m** of memory as minimum, with maximum of **2048m** to allow for the EM algorithm to run efficiently using `java -Xms1024m -Xmx2048m`. This is not unreasonable.

## Program Structure

- `IBM1.java` represents the IBM1 model. It uses `Counts.java` to get and put counts for c(e|f), c(e), c(j|ilm), c(ilm).
- `IBM2.java` represents the IBM2 model. It extends `IBM1.java`
- `Counts.java` keeps track of the 4 counts mentioned, simplying get and put operations. For c(j|ilm) and c(ilm), it hashes ilm into a single number, simplifying and speed up retrieval.
- `Unscramble.java` deals with part 6.

Both `IBM1.java` and `IBM2.java` can have their t-table and q-table serialized onto the harddisk for quicker testing. However, `run.sh` runs the program in its full glory.

`Test.java` is the test procedure we run for the sake of this homework. It does

1. Runs IBM1 model by reading in corpuses, initializing t values, and running EM on the t values.
2. Passes IBM1's t tables to IBM2.
3. IBM2 initializes q values, runs EM on both the t values and the q values.
4. IBM2's object gets passed to Unscramble, which uses the t table and q table to unscramble sentences.

This is only one of the possible ways to use this code. Too lazy to document the rest since this fulfills the homework.

## Runtime

Runtime is fast for EM due to data structure optimization. For example, I wrapped the counts in a Counts class that is built on hashtables, and hashed `i, l, m` such that the three integers can be hashed to a single number.

All operations for question 4 5 and 6 takes around 6 minutes on my computer.

## Results

### Question 4

The data structure for this question is optimized using hashtables and a Count class.

#### 4.1

No answer required.

#### 4.2

Answers are recorded to `ibm1_devwords_ranking.txt`. Reproduced here.

```
i
[('ich', 0.6981572060006594), (',', 0.07326584837825492), ('.', 0.036776505426203907), ('m&ouml;chte', 0.029566345831799637), ('da&szlig;', 0.026533463299233097), ('habe', 0.02527610662290237), ('mich', 0.013402890773210165), ('mir', 0.008514254028196529), ('die', 0.006758153623497956), ('zu', 0.006354108825664808)]

dog
[('delendum', 0.07261346244657423), ('stehen', 0.07251716723171572), ('darf', 0.07215885197604975), ('jetzt', 0.07036208931055091), ('einmal', 0.06786813416461689), ('wie', 0.06079417266851488), ('auf', 0.0513227529033191), ('das', 0.027990048619379978), ('sagen', 0.01980560600536974), ('ich', 0.01181370692256511)]

man
[('mann', 0.26940417677321893), ('mensch', 0.11399949085425883), ('wie', 0.09507784203841149), ('ehrenwerter', 0.04546470849293031), (',', 0.02660711147919008), ('man', 0.02573413427404919), ('ein', 0.020784940458512507), ('mu&szlig;', 0.020516631463233476), ('reagiert', 0.014306413768832422), ('f&uuml;r', 0.014018068651909863)]

keys
[('herr', 0.05605486929194048), ('faktors', 0.05143731714773572), ('33', 0.05143731710880044), ('steuerliche', 0.051437317099800275), ('verringert', 0.051437316906606564), ('belastung', 0.051437315262231), ('sch&ouml;n', 0.05143731204505374), ('cassidy', 0.05143469084784398), ('durchgef&uuml;hrt', 0.05143444530034483), ('punkt', 0.051313881719488266)]

bill
[('rechnung', 0.16404081962537084), ('hotelrechnung', 0.08029920105155336), ('sitzungswoche', 0.08029920100230725), ('pr&auml;sentiert', 0.0802162853918628), ('gegen&uuml;ber', 0.07777564401542358), ('mir', 0.0669550670990848), ('einem', 0.054513185188260205), ('nur', 0.04295041989394751), ('unzureichendes', 0.027959269606082693), ('bezahlt', 0.027874503485047848)]

naming
[('soll', 0.1557269600640017), ('erw&auml;hnung', 0.13603404937583663), ('meinen', 0.13168285102743404), ('herstellernamens', 0.07306280436573363), ('gedacht', 0.0730625052013302), ('erg&auml;nzung', 0.07232000158575562), ('beunruhigt', 0.03573320361266763), ('erscheinen', 0.035733098481096494), ('tatsache', 0.035627654996578224), ('berichterstatterin', 0.03496487823404974)]

anxiety
[('besorgnis', 0.1190752452129523), ('ausdruck', 0.08991715521729868), ('st&auml;rker', 0.07914631312125094), ('strecke', 0.0645054908662433), ('existiert', 0.06450526645965413), ('angst', 0.0644989241301537), ('bleiben', 0.061602992210871343), ('zunehmen', 0.054419989443615535), ('saal', 0.04721025391643052), ('liegt', 0.046043240810929115)]

junta
[('milit&auml;rjunta', 0.4063332274242208), ('junta', 0.25579889992002464), ('hatte', 0.024718917114127093), ('festgenommen', 0.02294681679366123), ('der', 0.022318151906458587), ('herrschenden', 0.01947836431868021), ('die', 0.01806753054781892), ('man', 0.01552578998761527), ('&ouml;lvorr&auml;te', 0.011123536908518398), ('besitzen', 0.011123518551849919)]

mediator
[('vermittler', 0.2049104284361347), ('ansonsten', 0.06788086339198357), ('einzig', 0.06783228143993215), ('betrachten', 0.06774769008679574), ('m&ouml;glichen', 0.06279916128934826), ('dorthin', 0.06211038744644851), ('stellen', 0.06011811364890441), ('spricht', 0.053299598511353005), ('entsandt', 0.05325284122375641), ('t&auml;glich', 0.049098965261157625)]

tribunal
[('gerichtshof', 0.1921694141977287), ('kriegsgericht', 0.12444528418659011), ('tribunal', 0.07188161687374238), ('gab', 0.07020691141344405), ('ein', 0.061792642341421324), ('dem', 0.035233122445254964), ('internationalen', 0.029222119558765445), ('uneingeschr&auml;nkt', 0.026879365234158844), ('g&uuml;ltig', 0.02685978614639516), ('daher', 0.02052277743811667)]

anniversary
[('jahrestag', 0.3524416516889406), ('zehnten', 0.11948813406443876), ('um', 0.06645178872456758), ('redet', 0.0303206873603335), ('so', 0.02914528702340467), ('alleine', 0.029142763684210243), ('manche', 0.029122735917302918), ('am', 0.027757186399920798), ('gar', 0.026484113775613046), ('zehnj&auml;hrige', 0.02460435975265256)]

dimension
[('dimension', 0.6704835983205725), ('der', 0.02156029239044303), ('die', 0.019395890853864207), ('besch&auml;ftigungsinitiative', 0.018401677990748308), ('sozialfonds', 0.017081591406204633), ('folglich', 0.01637482201535423), ('geboten', 0.016093889900755054), ('erscheint', 0.015552290151573153), ('wichtiger', 0.014011904736822092), ('wichtiges', 0.010597519202012597)]

depicted
[('wieder', 0.19805994929768306), ('immer', 0.19742281514999585), ('differenzierten', 0.05045988137211324), ('ansonsten', 0.050459860024676034), ('gef&auml;llt', 0.050459194415781075), ('dargestellt', 0.050453479105477365), ('gut', 0.04899090449466946), ('zeichnet', 0.04362999035300293), ('mir', 0.04336207707082061), ('er', 0.03597461770551627)]

prefers
[('beibeh&auml;lt', 0.06078906617004751), ('seiner', 0.060236387442213386), ('akzeptieren', 0.06012839349608018), ('verfahren', 0.05622023843654757), ('wahl', 0.044295848419763854), ('&auml;hnliche', 0.04286274539469315), ('spezifischen', 0.04286254817225314), ('inhaltlich', 0.042861978224606564), ('standpunkte', 0.042861699417829024), ('kompromi&szlig;', 0.04286100182068207)]

visa
[('l&auml;nder', 0.05015915458503217), ('visumpflicht', 0.04278112640258922), ('erweiterte', 0.04278112629872585), ('versch&auml;rft', 0.0427372018435898), ('wird', 0.03585288683091732), ('visumbestimmungen', 0.03580287096775996), ('harmonisiert', 0.03580286371134456), ('einreise', 0.035802843476010225), ('personen', 0.03580270885303615), ('erlaubt', 0.035802032589798874)]

wood
[('viel', 0.08358011707987399), ('w&auml;ldern', 0.07792740015562957), ('finnischen', 0.0779273052755994), ('nutzt', 0.07792702600292752), ('liegen', 0.07782602622572708), (';', 0.07279849503524365), ('riesige', 0.06912248182325018), ('wald', 0.0639626936339244), ('beispielsweise', 0.03605769635493934), ('jedoch', 0.028086065105268104)]

agent
[('verringern', 0.15535395999159568), ('ber&uuml;hrung', 0.1146241190635139), ('kommen', 0.11286628466037647), ('sind', 0.03914706285130015), ('erreger', 0.032913494471428675), ('ausschlie&szlig;t', 0.032913332696070614), ('gef&auml;hrdung', 0.03291320127933618), ('lie&szlig;e', 0.032911254757731066), ('gekommen', 0.03289656896368602), ('wahrscheinlich', 0.03184934854253862)]

consume
[('viel', 0.08764428028363065), ('energie', 0.07459866186637784), ('uns', 0.0735259543283446), ('verbrauchen', 0.06288675156081777), ('verbraucht', 0.05306387172656224), ('papierindustrie', 0.026548584559623663), ('f&uuml;rs', 0.02654858308671613), ('schwerpunkt', 0.02654705583419504), ('nordischen', 0.026537856583999563), ('seinen', 0.02537870730152274)]

everyday
[('normalerweise', 0.10892296199721334), ('funktioniert', 0.10860322247453268), ('alltags', 0.10830812261567553), ('hinaus', 0.10815850199731833), ('zolls', 0.107842159133845), ('drogenbek&auml;mpfung', 0.10653352335911888), ('dar&uuml;ber', 0.10402096258969973), ('des', 0.09307949555939501), ('gef&auml;ngnisstrafe', 0.03324121823583096), ('normalen', 0.03324120939882426)]

fix
[('bescheinigen', 0.09253746086684626), ('festlegen', 0.09253736057766589), ('gegeben', 0.08277952306558964), ('einen', 0.06159739675402539), ('abhaltung', 0.042331408909216266), ('abgekartete', 0.03974100231565171), ('zeitalter', 0.03974100225851598), ('schnelle', 0.03974099985030944), ('aufgeben', 0.03974099670719956), ('t&auml;tig', 0.039613593311930255)]

ocean
[('tropfen', 0.17151858425119354), ('hei&szlig;en', 0.1714925025056699), ('dar&uuml;ber', 0.09013213637631637), ('klaren', 0.08360886331849485), ('uns', 0.03116266446407473), ('m&uuml;ssen', 0.031137098788845574), ('sind', 0.03025154391367905), ('sein', 0.03010326535817631), ('auf', 0.028713309206372776), ('ein', 0.02720760646489627)]
```

#### 4.3

Answers are recorded to `ibm1_alignment.txt`. Reproduced here. Are exactly the same as the sample answers provided.

```
resumption of the session
wiederaufnahme der sitzungsperiode
[1, 2, 4]

i declare resumed the session of the european parliament adjourned on thursday , 28 march 1996 .
ich erkl&auml;re die am donnerstag , den 28. m&auml;rz 1996 unterbrochene sitzungsperiode des europ&auml;ischen parlaments f&uuml;r wiederaufgenommen .
[1, 2, 4, 12, 12, 13, 4, 14, 15, 16, 2, 5, 10, 8, 9, 2, 3, 17]

welcome
begr&uuml;&szlig;ung
[1]

i bid you a warm welcome !
herzlich willkommen !
[5, 2, 7]

approval of the minutes
genehmigung des protokolls
[1, 1, 1]

the minutes of the sitting of thursday , 28 march 1996 have been distributed .
das protokoll der sitzung vom donnerstag , den 28. m&auml;rz 1996 wurde verteilt .
[2, 2, 3, 5, 10, 7, 8, 1, 9, 10, 11, 14, 14, 15]

are there any comments ?
gibt es einw&auml;nde ?
[2, 2, 4, 5]

points 16 and 17 now contradict one another whereas the voting showed otherwise .
die punkte 16 und 17 widersprechen sich jetzt , obwohl es bei der abstimmung anders aussah .
[10, 1, 2, 3, 4, 6, 9, 5, 14, 6, 6, 4, 10, 11, 6, 6, 14]

i shall be passing on to you some comments which you could perhaps take up with regard to the voting .
ich werde ihnen die entsprechenden anmerkungen aush&auml;ndigen , damit sie das eventuell bei der abstimmung &uuml;bernehmen k&ouml;nnen .
[1, 2, 7, 19, 4, 9, 4, 10, 20, 7, 0, 4, 17, 19, 20, 4, 12, 21]

i will have to look into that , mrs oomen-ruijten .
das mu&szlig; ich erst einmal kl&auml;ren , frau oomen-ruijten .
[7, 5, 1, 10, 5, 10, 8, 9, 10, 11]

i cannot say anything at this stage .
das kann ich so aus dem stand nicht sagen .
[6, 2, 1, 4, 4, 7, 7, 2, 3, 8]

we will consider the matter .
wir werden das &uuml;berpr&uuml;fen .
[1, 2, 0, 3, 6]

mr president , it concerns the speech made last week by mr fischler on bse and reported in the minutes .
es geht um die erkl&auml;rung von herrn fischler zu bse , die im protokoll festgehalten wurde .
[4, 13, 17, 6, 17, 11, 1, 13, 14, 15, 3, 6, 18, 20, 17, 9, 21]

perhaps the commission or you could clarify a point for me .
vielleicht k&ouml;nnten die kommission oder sie mir einen punkt erl&auml;utern .
[1, 6, 2, 3, 4, 5, 11, 8, 9, 7, 12]

it would appear that a speech made at the weekend by mr fischler indicates a change of his position .
offensichtlich bedeutet die erkl&auml;rung von herrn fischler vom wochenende eine &auml;nderung der haltung der kommission .
[3, 13, 9, 10, 11, 12, 13, 3, 10, 5, 16, 17, 19, 17, 10, 20]

i welcome this change because he has said that he will eat british beef and that the ban was imposed specifically for economic and political reasons .
ich begr&uuml;&szlig;e diese &auml;nderung , denn er sagte , da&szlig; er britisches rindfleisch essen w&uuml;rde und da&szlig; das einfuhrverbot insbesondere aus wirtschaftlichen und politischen gr&uuml;nden verh&auml;ngt wurde .
[1, 2, 3, 4, 9, 5, 6, 8, 9, 9, 6, 12, 14, 12, 12, 15, 9, 18, 12, 21, 26, 23, 15, 25, 26, 12, 19, 27]

could somebody clarify that he has actually said this please , mr president , because it is a change of views .
herr pr&auml;sident , k&ouml;nnte festgestellt werden , ob er das tats&auml;chlich gesagt hat , denn das w&uuml;rde eine &auml;nderung der haltung der kommission bedeuten .
[12, 13, 11, 1, 2, 2, 11, 2, 5, 17, 7, 8, 6, 11, 15, 17, 2, 18, 19, 20, 2, 20, 2, 2, 22]

mr sturdy , i cannot see what that has to do with the minutes .
herr kollege , ich kann nicht erkennen , was das mit dem protokoll zu tun hat .
[1, 2, 3, 4, 5, 5, 2, 3, 7, 14, 12, 12, 14, 10, 11, 9, 15]

mr president , on exactly the same point as mr sturdy has raised .
herr pr&auml;sident , zum gleichen punkt , den auch herr sturdy angesprochen hat .
[1, 2, 3, 11, 7, 8, 3, 6, 11, 1, 11, 13, 12, 14]

if commission fischler has made this statement , then he has said that it is not a matter of public health .
wenn herr kommissar fischler diese erkl&auml;rung abgegeben hat , dann bedeutet dies , da&szlig; es sich nicht um eine angelegenheit der &ouml;ffentlichen gesundheit handelt .
[1, 7, 3, 3, 6, 7, 7, 4, 8, 9, 3, 6, 8, 13, 14, 4, 16, 18, 17, 18, 19, 20, 21, 3, 22]
```

### Question 5

IBM2 is a subclass of IBM1 since they share a lot of common features

To speed up testing, I have allowed for IBM1 and IBM2 to serialize and deserialize the t-tables and q-tables. They are only used during testing. For the `run.sh` run, I chose to generate them on the spot since the runtime is suaully under 7 minutes.

#### 5.1

No answer required. 

#### 5.2

No answer required.

#### 5.3

Answer printed to `ibm2_alignment.txt`. Reproduced here

```
resumption of the session
wiederaufnahme der sitzungsperiode
[1, 3, 4]

i declare resumed the session of the european parliament adjourned on thursday , 28 march 1996 .
ich erkl&auml;re die am donnerstag , den 28. m&auml;rz 1996 unterbrochene sitzungsperiode des europ&auml;ischen parlaments f&uuml;r wiederaufgenommen .
[1, 2, 3, 12, 12, 6, 6, 14, 15, 16, 10, 5, 3, 8, 9, 16, 3, 17]

welcome
begr&uuml;&szlig;ung
[1]

i bid you a warm welcome !
herzlich willkommen !
[5, 2, 7]

approval of the minutes
genehmigung des protokolls
[1, 2, 4]

the minutes of the sitting of thursday , 28 march 1996 have been distributed .
das protokoll der sitzung vom donnerstag , den 28. m&auml;rz 1996 wurde verteilt .
[1, 2, 3, 5, 10, 7, 8, 4, 9, 10, 11, 14, 14, 15]

are there any comments ?
gibt es einw&auml;nde ?
[2, 2, 4, 5]

points 16 and 17 now contradict one another whereas the voting showed otherwise .
die punkte 16 und 17 widersprechen sich jetzt , obwohl es bei der abstimmung anders aussah .
[10, 1, 2, 3, 4, 6, 6, 5, 10, 9, 10, 12, 10, 11, 12, 6, 14]

i shall be passing on to you some comments which you could perhaps take up with regard to the voting .
ich werde ihnen die entsprechenden anmerkungen aush&auml;ndigen , damit sie das eventuell bei der abstimmung &uuml;bernehmen k&ouml;nnen .
[1, 2, 7, 3, 4, 9, 4, 10, 4, 11, 19, 13, 17, 19, 20, 20, 18, 21]

i will have to look into that , mrs oomen-ruijten .
das mu&szlig; ich erst einmal kl&auml;ren , frau oomen-ruijten .
[7, 2, 1, 10, 5, 10, 8, 9, 10, 11]

i cannot say anything at this stage .
das kann ich so aus dem stand nicht sagen .
[5, 2, 3, 6, 4, 5, 7, 2, 3, 8]

we will consider the matter .
wir werden das &uuml;berpr&uuml;fen .
[1, 2, 4, 5, 6]

mr president , it concerns the speech made last week by mr fischler on bse and reported in the minutes .
es geht um die erkl&auml;rung von herrn fischler zu bse , die im protokoll festgehalten wurde .
[4, 5, 3, 6, 7, 7, 7, 13, 14, 15, 14, 15, 17, 20, 17, 20, 21]

perhaps the commission or you could clarify a point for me .
vielleicht k&ouml;nnten die kommission oder sie mir einen punkt erl&auml;utern .
[1, 6, 2, 3, 4, 5, 11, 8, 9, 7, 12]

it would appear that a speech made at the weekend by mr fischler indicates a change of his position .
offensichtlich bedeutet die erkl&auml;rung von herrn fischler vom wochenende eine &auml;nderung der haltung der kommission .
[3, 14, 3, 6, 7, 6, 13, 11, 10, 15, 16, 17, 19, 17, 14, 20]

i welcome this change because he has said that he will eat british beef and that the ban was imposed specifically for economic and political reasons .
ich begr&uuml;&szlig;e diese &auml;nderung , denn er sagte , da&szlig; er britisches rindfleisch essen w&uuml;rde und da&szlig; das einfuhrverbot insbesondere aus wirtschaftlichen und politischen gr&uuml;nden verh&auml;ngt wurde .
[1, 2, 3, 4, 6, 5, 10, 8, 8, 9, 10, 12, 14, 12, 12, 15, 16, 18, 18, 21, 26, 23, 24, 25, 26, 12, 19, 27]

could somebody clarify that he has actually said this please , mr president , because it is a change of views .
herr pr&auml;sident , k&ouml;nnte festgestellt werden , ob er das tats&auml;chlich gesagt hat , denn das w&uuml;rde eine &auml;nderung der haltung der kommission bedeuten .
[12, 2, 4, 1, 2, 4, 7, 2, 5, 9, 7, 8, 6, 14, 15, 16, 2, 18, 19, 20, 19, 20, 21, 2, 22]

mr sturdy , i cannot see what that has to do with the minutes .
herr kollege , ich kann nicht erkennen , was das mit dem protokoll zu tun hat .
[1, 2, 3, 4, 5, 5, 2, 8, 7, 8, 12, 10, 14, 12, 11, 9, 15]

mr president , on exactly the same point as mr sturdy has raised .
herr pr&auml;sident , zum gleichen punkt , den auch herr sturdy angesprochen hat .
[1, 2, 3, 4, 7, 8, 3, 6, 9, 10, 11, 13, 12, 14]

if commission fischler has made this statement , then he has said that it is not a matter of public health .
wenn herr kommissar fischler diese erkl&auml;rung abgegeben hat , dann bedeutet dies , da&szlig; es sich nicht um eine angelegenheit der &ouml;ffentlichen gesundheit handelt .
[1, 7, 3, 3, 6, 7, 7, 5, 8, 9, 7, 6, 12, 13, 14, 11, 16, 18, 17, 18, 19, 20, 21, 21, 22]
```

### Question 6

Answer written to `unscrambled.en`

Evaluation produces

```
Right   Total   Acc
92  100 0.920
```

Difficult part of this section is choosing the right "large negative number" for words not found in the *t* or *q* tables. Via optimization on the evaluation results, I found this large negative number to be around `-353916369`. 

I didn't remove English sentences that are already chosen for an earlier German sentence. This didn't seem to have too much of a problem. Removal actually results in a lower evaluation score.