import HW4.*;

//DO NOT IMPORT BigInteger (or any other implementation of BigInteger) from the standard java library or some other place. 
//ONLY use BigInt as provided in the HW4 package.


public class RSAStudent implements RSAInterface{

	/**
	 * Main function: please modify for your own testing purposes. We will use a different one for grading
	 * Feel free to play with larger prime numbers!!!  
	 */
	public static void main(String[] args) {
		RSAStudent rsa = new RSAStudent();
		BigInt p = new BigInt("5333");
		BigInt q = new BigInt("6101");
		Pair<PublicKey, PrivateKey> keys;
		
		keys = rsa.generateKey(p,q);
				
		//IF YOU CAN'T GET generateKey TO WORK,  USE THE FOLLOWING AS YOUR KEYS
		//keys = new Pair<PublicKey, PrivateKey>
		//			( new PublicKey(new BigInt("32536633"), new BigInt("3")), 
		//		  new PrivateKey(new BigInt("21683467")));
		//...TO HERE
		
		BigInt[] E = rsa.encryptASCII("smile because it happened!", keys.fst);
		System.out.println("Don\'t cry because it\'s over..." + 
				rsa.decryptASCII(E, keys.fst, keys.snd));
		System.out.println();
		
		E = rsa.encryptASCII("depending how far beyond zebra you go.", keys.fst);
		System.out.println("There\'s no limit to how much you'll know..." + 
				rsa.decryptASCII(E, keys.fst, rsa.hackKey(keys.fst)));	

		
	}
	
	
	/**
	 * Will encrypt the ASCII code of each character in a given String
	 * @param pMessage : the Message to encrypt
	 * @param pPublic : the public key to use for encryption
	 * @return : element at position i represents the encryption of character at position i in 
	 * the input string
	 */
	public BigInt[] encryptASCII(String pMessage, PublicKey pPublic) {
		BigInt[] toRet = new BigInt[pMessage.length()];
		for (int i=0; i < toRet.length; i++) {
			toRet[i] = encrypt(new BigInt((int) pMessage.charAt(i)), pPublic);			
		}
		return toRet;
	}
	
	/**
	 * Will decrypt a list of integers to ASCII code of each character, and then convert it to a String
	 * @param pEncryptedMessage : the list of encrypted ASCII codes
	 * @param pPublic : the public key that was used to encrypt the message
	 * @param pPrivate : the private key used by the receiver for decryption
	 * @return the String encrypted in the input list
	 */
	public String decryptASCII(BigInt[] pEncryptedMessage, PublicKey pPublic, PrivateKey pPrivate) {
		String s = "";
		for (int i=0; i< pEncryptedMessage.length ; i++) {			
			BigInt val = decrypt(pEncryptedMessage[i], pPublic, pPrivate);
			s += val.toAscii();
		}
		return s;
	}


	@Override
	public BigInt encrypt(BigInt pMessage, PublicKey pPublic)
	{
		BigInt hiddenmessage = fastModularExpo(pMessage, pPublic.exponent , pPublic.modulus);
		return hiddenmessage;
	}


	@Override
	public BigInt decrypt(BigInt pEncryptedMessage, PublicKey pPublic, PrivateKey pPrivate)
	{
		BigInt unhiddenmessage = fastModularExpo(pEncryptedMessage, pPrivate.key , pPublic.modulus);
		return unhiddenmessage;
	}


	@Override
	public Pair<PublicKey, PrivateKey> generateKey(BigInt pPrimeNo1, BigInt pPrimeNo2)
	{
		BigInt n = pPrimeNo1.multiply(pPrimeNo2);
		BigInt e = new BigInt((int)Math.pow(2,16)+1);
		BigInt totientn = pPrimeNo1.subtract(new BigInt("1")).multiply(pPrimeNo2.subtract(new BigInt("1")));
		PublicKey publickey = new PublicKey(n , e);
		BigInt d = e.modInverse(totientn);
		PrivateKey privatekey = new PrivateKey(d);
		Pair<PublicKey, PrivateKey> ret = new Pair<PublicKey, PrivateKey>(publickey, privatekey);
		return ret;
	}


	@Override
	public BigInt fastModularExpo(BigInt pBase, BigInt pExponent, BigInt pModulus)
	{
		BigInt zero = new BigInt("0");
		BigInt one = new BigInt("1");
		BigInt two = new BigInt("2");
		BigInt n = new BigInt("1");
		while(!(pExponent.equals(zero)))
		{
			if(pExponent.mod(two).equals(zero))
			//if the exponent is even
			{
				pBase = pBase.multiply(pBase);
				pBase = pBase.mod(pModulus);
				pExponent = pExponent.half();
			}
			else
			{
				n = n.multiply(pBase);
				n = n.mod(pModulus);
				pExponent = pExponent.subtract(one);
			}
		}
		return n;
	}


	@Override
	public PrivateKey hackKey(PublicKey pPublic)
	{
		boolean lessThanN = true;
		BigInt count = new BigInt("2");
		BigInt zero = new BigInt("0");
		while(lessThanN)
		{
			if(pPublic.modulus.mod(count).equals(zero) && isPrime(count))
			{
				break;
			}
			count.increment();
			if(count.subtract(pPublic.exponent).equals(zero))
			{
				lessThanN = false;
			}
		}
		Pair<PublicKey , PrivateKey> keys = generateKey(count , pPublic.modulus.div(count));
		return keys.snd;
	}


	@Override
	public boolean isPrime(BigInt p)
	{
		BigInt[] random = new BigInt[5];
		BigInt one = new BigInt("1");
		for(int i = 0 ; i < random.length ; i++)
		{
			random[i] = BigInt.generateRandom(p);
		}
		for(BigInt x : random)
		{
			BigInt power = fastModularExpo(x , p.subtract(one) , p);
			if(!(power.equals(one)))
			{
				return false;
			}
		}
		return true;
	}
	
}
