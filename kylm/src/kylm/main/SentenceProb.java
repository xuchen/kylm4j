/**
 *
 */
package kylm.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import kylm.model.LanguageModel;
import kylm.model.ngram.NgramLM;
import kylm.model.ngram.reader.ArpaNgramReader;
import kylm.model.ngram.reader.NgramReader;
import kylm.model.ngram.reader.SerializedNgramReader;
import kylm.reader.TextStreamSentenceReader;
import kylm.util.KylmConfigUtils;
import kylm.util.KylmTextUtils;

/**
 * @author Xuchen Yao
 *
 */
public class SentenceProb {

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		final String br = System.getProperty("line.separator");
		KylmConfigUtils config = new KylmConfigUtils(
				"RandomSentences"+br+
				"A program to calculate probabilities of sentences given an Ngram lanugage model"+br+
		"Example: java -cp kylm.jar kylm.main.SentenceProb -arpa model1.arpa test.txt");

		// Input format options
		config.addEntry("arpa", KylmConfigUtils.STRING_TYPE, null, true, "model in arpa format");
		config.addEntry("bin", KylmConfigUtils.STRING_TYPE, null, false, "model in binary format");

		// parse the arguments
		args = config.parseArguments(args);
		if(args.length != 1)
			config.exitOnUsage();

		// read in the model
		System.err.println("Reading model");
		String lmFile = config.getString("arpa");
		NgramReader nr;
		if (lmFile==null) {
			lmFile = config.getString("bin");
			nr = new SerializedNgramReader();
		} else
			nr = new ArpaNgramReader();
		NgramLM lm = null;
		try { lm = nr.read(lmFile); } catch(IOException e) {
			System.err.println("Problem reading model from file "+lmFile+": "+e.getMessage());
			System.exit(1);
		}

		// get the input stream to load the input
		InputStream is = (args.length == 0?System.in:new FileInputStream(args[0]));
		TextStreamSentenceReader tssl = new TextStreamSentenceReader(is);

		for(String[] sent : tssl) {
			float prob = lm.getSentenceProb(sent);
			System.out.println("Log likelihood of sentence \""+KylmTextUtils.concatWithSpaces(sent)+
					"\": "+prob+"("+prob/sent.length+")");
		}
	}

}
