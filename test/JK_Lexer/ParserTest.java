package JK_Lexer;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParserTest {
    // specify null for expected if it's not supposed to parse
    public void assertParses(final Token[] tokens,
                             final Exp expected) {
        final Parser parser = new Parser(tokens);
        try {
            final Exp received = parser.parseExp();
            assertTrue("Expected parse failure; got: " + received,
                       expected != null);
            assertEquals(expected, received);
        } catch (final ParserException e) {
            assertTrue(("Unexpected parse failure for " +
                        Arrays.toString(tokens) +
                        ": " + e.getMessage()),
                       expected == null);
        }
    } // assertParses
    
    public void assertParsesConstructorDef(final Token[] tokens, final ConstructorDef expected) {
    	final Parser parser = new Parser(tokens);
    	try {
    		final ConstructorDef received = parser.parseConstructorDef(expected.name);
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
}
    
    public void assertParsesClassDef(final Token[] tokens, final ClassDefExp expected) {
    	final Parser parser = new Parser(tokens);
    	try {
    		final ClassDefExp received = parser.parseClassDef();
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
}
    
    
    public void assertParsesProgram(final Token[] tokens, final Program expected) {
    	final Parser parser = new Parser(tokens);
    	try {
    		final Program received = parser.parseProgram();
    		assertTrue("Expected parse failure; got: " + received, expected != null);
    		assertEquals(expected, received);
    	} catch (final ParserException e) {
    		assertTrue(("Unexpected parse failure for " + Arrays.toString(tokens) + ": " + e.getMessage()), expected == null);
    	}
}
    
    
    @Test
    public void testParsesInteger() {
        assertParses(new Token[]{ new NumberToken(123) },
                     new NumberExp(123));
    }

    @Test
    public void testParsesVariable() {
        assertParses(new Token[]{ new NameToken("foo") },
                     new VariableExp("foo"));
    }

    @Test
    public void testParsesParens() {
        final Token[] tokens = { new LeftParenToken(),
                                 new NumberToken(1),
                                 new RightParenToken() };
        final Exp expected = new NumberExp(1);
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesPlus() {
        final Token[] tokens = { new NumberToken(1),
                                 new PlusToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new PlusOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesMinus() {
        final Token[] tokens = { new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MinusOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesMult() {
        final Token[] tokens = { new NumberToken(1),
                                 new MultToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MultOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testParsesDiv() {
        final Token[] tokens = { new NumberToken(1),
                                 new DivToken(),
                                 new NumberToken(2) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new DivOp(),
                                          new NumberExp(2));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticLeftAssociative() {
        final Token[] tokens = { new NumberToken(1),
                                 new PlusToken(),
                                 new NumberToken(2),
                                 new MinusToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new BinopExp(new NumberExp(1),
                                                       new PlusOp(),
                                                       new NumberExp(2)),
                                          new MinusOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticPrecedence() {
        final Token[] tokens = { new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2),
                                 new DivToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new NumberExp(1),
                                          new MinusOp(),
                                          new BinopExp(new NumberExp(2),
                                                       new DivOp(),
                                                       new NumberExp(3)));
        assertParses(tokens, expected);
    }

    @Test
    public void testArithmeticPrecedenceWithParens() {
        final Token[] tokens = { new LeftParenToken(),
                                 new NumberToken(1),
                                 new MinusToken(),
                                 new NumberToken(2),
                                 new RightParenToken(),
                                 new DivToken(),
                                 new NumberToken(3) };
        final Exp expected = new BinopExp(new BinopExp(new NumberExp(1),
                                                       new MinusOp(),
                                                       new NumberExp(2)),
                                          new DivOp(),
                                          new NumberExp(3));
        assertParses(tokens, expected);
    }
    @Test
    public void testString() {
    	final Token[] tokens = { new QuoteToken(), 
    							 new QuotedStringToken("woo"),
    							 new QuoteToken() }; 
    	final Exp expected = new StringExp("woo");
    	assertParses(tokens, expected); 
    }
    @Test
    public void testThis() {
    	final Token[] tokens = { new ThisToken(), 
    							 new PeriodToken(), 
    							 new NameToken("boo") }; 
    	final Exp expected = new ThisExp(new VariableExp("boo")); 
    	assertParses(tokens, expected); 
    }
    @Test
    public void testPrint() {
    	final Token [] tokens = { new PrintToken(), 
    							  new LeftParenToken(), 
    							  new NameToken("String1"), 
    							  new RightParenToken(),
    							  new SemicolonToken()  }; 
    	final Exp expected = new PrintExp(new VariableExp("String1"));
    	assertParses(tokens, expected); 
    }
    @Test 
    public void testCallMethod() {
    	final Token[] tokens = { new NameToken("foo"),
    							 new PeriodToken(), 
    							 new NameToken("add"),
    							 new LeftParenToken(), 
    							 new NameToken("foo2"),
    							 new RightParenToken() }; 
    	final Exp expected = new CallMethodExp(new VariableExp("foo"),
    										   new VariableExp("add"),
    										   new VariableExp("foo2"));
    	assertParses(tokens,expected); 
    }
    @Test
    public void testNewClass() {
    	final Token[] tokens = { new NewToken(), 
    							 new PeriodToken(), 
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new NameToken("grade"),
    							 new RightParenToken() };
    	final Exp expected = new NewExp(new VariableExp("Student"),
    									new VariableExp("grade"));
    	assertParses(tokens, expected); 
    }
    
    @Test
    public void testConstructor() {
    	final Token[] tokens = { new PublicToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("foo"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new RightCurlyToken()};
    	ArrayList<VariableDecExp> param = new ArrayList<VariableDecExp>();
    	param.add(new VariableDecExp(new IntType(), new VariableExp("foo")));
    	final ConstructorDef expected = new ConstructorDef(new PublicModifier(), "Student",param , new ArrayList());
    	
    	assertParsesConstructorDef(tokens, expected); 
    }
    
    @Test
    public void testClassDec() {
    	final Token[] tokens = { new PublicToken(), 
    							 new ClassToken(), 
    							 new NameToken("Student"),
    							 new LeftCurlyToken(),
    							 new PrivateToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemicolonToken(),
    							 new PublicToken(),
    							 new IntToken(),
    							 new NameToken("getAge"),
    							 new LeftParenToken(),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new ReturnToken(),
    							 new NameToken("age"),
    							 new SemicolonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new VoidToken(),
    							 new NameToken("setAge"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("n"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new AssignmentToken(),
    							 new NameToken("n"),
    							 new SemicolonToken(),
    							 new RightCurlyToken(),
    							 new RightCurlyToken() };
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("n")));
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	final ClassDefExp expected = new ClassDefExp(new PublicModifier(), "Student", new ArrayList<ConstructorDef>(), memberVarList, methodList, false, "");
    	assertParsesClassDef(tokens, expected); 
    }
    
    
    @Test
    public void testProgram() {
    	final Token[] tokens = { new PublicToken(), 
    							 new ClassToken(), 
    							 new NameToken("Student"),
    							 new ExtendsToken(),
    							 new NameToken("Person"),
    							 new LeftCurlyToken(),
    							 new PrivateToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemicolonToken(),
    							 new PublicToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("a"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new AssignmentToken(),
    							 new NameToken("a"),
    							 new SemicolonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new IntToken(),
    							 new NameToken("getAge"),
    							 new LeftParenToken(),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new ReturnToken(),
    							 new NameToken("age"),
    							 new SemicolonToken(),
    							 new RightCurlyToken(),
    							 new PublicToken(),
    							 new VoidToken(),
    							 new NameToken("setAge"),
    							 new LeftParenToken(),
    							 new IntToken(),
    							 new NameToken("n"),
    							 new RightParenToken(),
    							 new LeftCurlyToken(),
    							 new NameToken("age"),
    							 new AssignmentToken(),
    							 new NameToken("n"),
    							 new SemicolonToken(),
    							 new RightCurlyToken(),
    							 new RightCurlyToken(),
    							 new IntToken(),
    							 new NameToken("age"),
    							 new SemicolonToken(),
    							 new NameToken("age"),
    							 new AssignmentToken(),
    							 new NumberToken(21),
    							 new SemicolonToken(),
    							 new NameToken("Student"),
    							 new NameToken("student"),
    							 new SemicolonToken(),
    							 new NameToken("student"),
    							 new AssignmentToken(),
    							 new NewToken(),
    							 new PeriodToken(),
    							 new NameToken("Student"),
    							 new LeftParenToken(),
    							 new NameToken("age"),
       							 new RightParenToken(),
    							 new SemicolonToken()};
    	ArrayList<InstanceDecExp> memberVarList = new ArrayList<InstanceDecExp>();
    	memberVarList.add(new InstanceDecExp(new PrivateModifier(), new VariableDecExp(new IntType(), new VariableExp("age"))));
    	ArrayList<MethodDefExp> methodList = new ArrayList<MethodDefExp>();
    	ArrayList<ConstructorDef> constructorList = new ArrayList<ConstructorDef>();
    	ArrayList<Statement> block = new ArrayList<Statement>();
    	ArrayList<Statement> setblock = new ArrayList<Statement>();
    	ArrayList<VariableDecExp> setparam = new ArrayList<VariableDecExp>();
    	ArrayList<VariableDecExp> constructorParam = new ArrayList<VariableDecExp>();
    	ArrayList<Statement> constructorblock = new ArrayList<Statement>();
    	constructorblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("a")));
    	constructorParam.add(new VariableDecExp(new IntType(), new VariableExp("a")));
    	setparam.add(new VariableDecExp(new IntType(), new VariableExp("n")));
    	block.add(new ReturnStmt(new VariableExp("age")));
    	setblock.add(new AssignmentStmt(new VariableExp("age"), new VariableExp("n")));
    	methodList.add(new MethodDefExp(new PublicModifier(), new IntType(), "getAge", new ArrayList<VariableDecExp>(), block));
    	methodList.add(new MethodDefExp(new PublicModifier(), new VoidType(), "setAge", setparam, setblock));
    	constructorList.add(new ConstructorDef(new PublicModifier(), "Student", constructorParam, constructorblock));
    	ClassDefExp classStudent = new ClassDefExp(new PublicModifier(), "Student", constructorList, memberVarList, methodList, true, "Person");
    	ArrayList<ClassDefExp> classDefList = new ArrayList<ClassDefExp>();
    	ArrayList<Statement> statementList = new ArrayList<Statement>();
    	classDefList.add(classStudent);
    	statementList.add(new VariableDecExp(new IntType(), new VariableExp("age")));
    	statementList.add(new AssignmentStmt(new VariableExp("age"), new NumberExp(21)));
    	statementList.add(new VariableDecExp(new ObjectType("Student"), new VariableExp("student")));
    	statementList.add(new AssignmentStmt(new VariableExp("student"), new NewExp(new VariableExp("Student"),new VariableExp("age"))));
    	
    	Program expected = new Program(statementList, classDefList);
    	assertParsesProgram(tokens, expected); 
    }
    
}