package JK_Lexer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner; 
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class CCodeGeneratorTest {
	//Testing method for basic exp generation: Create temp file and write an exp e to it, read file to a string, and compare it with expected string
	public void assertBasicExpGeneration(String expected, Exp e) throws IOException{
		try {
			CCodeGenerator cg = new CCodeGenerator(); 
			final File file = File.createTempFile("testfile",".c");
			cg.writeExpressiontoFile(e, file);
			final String received = convertFileToString(file); 
			assertTrue("Expected code generation error: "+received, expected!=null); 
			assertEquals(expected,received); 
			file.delete(); 
		}catch(final CCodeGeneratorException exc) {
			assertTrue("Unexpected code generation error: "+exc.getMessage(), expected==null);
		}
	}
	//Method for converting text from the c file created to a string, might have to change for later testing with more than one line/exp
	public String convertFileToString(File file) throws IOException{
		StringBuilder sb = new StringBuilder((int)file.length());
		Scanner scanner = new Scanner(file);
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
		}
		return sb.toString(); 
	}
	@Test
	public void testNumberExp() throws IOException{
		assertBasicExpGeneration("1", new NumberExp(1));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestNumberExp() throws IOException{
		assertBasicExpGeneration("2", new NumberExp(1));
	}
	@Test 
	public void testStringExp() throws IOException{
		assertBasicExpGeneration("hello world", new StringExp("hello world"));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestStringExp() throws IOException{
		assertBasicExpGeneration("goodbye world", new StringExp("hello world"));
	}
	@Test 
	public void testVariableExp() throws IOException{
		assertBasicExpGeneration("varname21", new VariableExp("varname21"));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestVariableExp() throws IOException{
		assertBasicExpGeneration("varname2100", new VariableExp("varname21"));
	}
	@Test 
	public void testBinopPlus() throws IOException{
		assertBasicExpGeneration("1+2", new BinopExp(new NumberExp(1), new PlusOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopMinus() throws IOException{
		assertBasicExpGeneration("3-2", new BinopExp(new NumberExp(3), new MinusOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopMult() throws IOException{
		assertBasicExpGeneration("3*2", new BinopExp(new NumberExp(3), new MultOp(), new NumberExp(2)));
	}
	@Test 
	public void testBinopDiv() throws IOException{
		assertBasicExpGeneration("4/2", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestBinopWrongOp() throws IOException{
		assertBasicExpGeneration("4+2", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test(expected = ComparisonFailure.class) 
	public void failsTestBinopWrongNumber() throws IOException{
		assertBasicExpGeneration("4/4", new BinopExp(new NumberExp(4), new DivOp(), new NumberExp(2)));
	}
	@Test
	public void testPrintF() throws IOException{
		assertBasicExpGeneration("printf(var1)", new PrintExp(new VariableExp("var1")));
	}
	@Test(expected = ComparisonFailure.class)
	public void failsTestPrintF() throws IOException{
		assertBasicExpGeneration("printf(var2)", new PrintExp(new VariableExp("var1")));
	}
	
	//Ignore this last test, just testing if the CCodeGeneratorException is working in compileExp
	@Test(expected = AssertionError.class)
	public void failsCodeGen() throws IOException{
		assertBasicExpGeneration("var", new ThisExp(new VariableExp("var")));
	}
}