package starunit.runner;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Enumeration;

import starunit.framework.AssertionFailedError;
import starunit.framework.Test;
import starunit.framework.TestFailure;
import starunit.framework.TestListener;
import starunit.framework.TestResult;

public class ResultPrinter implements TestListener {
	PrintStream fWriter;
	int fColumn = 0;
	
	public ResultPrinter(PrintStream writer){
		fWriter = writer;
	}
	/*
	 *  API for use by textui.TestRunner
	 */
	synchronized void print(TestResult result,long runTime){
		printHeader(runTime);
		printErrors(result);
		printFailures(result);
		printFooter(result);
	}
	protected void printHeader(long runTime){
		getWriter().println();
		getWriter().println("Time" + elapsedTimeAsString(runTime));
	}
	
	protected void printFailures(TestResult result){
		printDefects(result.failures(),result.failureCount(),"failure");
	}
	protected void printErrors(TestResult result){
		printDefects(result.errors(),result.errorCount(),"error");
	}
	/*
	 * Returns the formatted string of the elapsed time
	 */
	protected String elapsedTimeAsString(long runTime){
		return NumberFormat.getInstance().format((double)runTime/1000);
	}
	public PrintStream getWriter(){
		return fWriter;
	}
	
	public void addError(Test test, Throwable t) {

		getWriter().print("E");
	}


	public void addFailuer(Test test, AssertionFailedError t) {
		getWriter().print("F");
	}


	public void endTest(Test test) {
	}

	public void startTest(Test test) {
		getWriter().print(".");
		/*if(fColumn ++ >= 40){
			getWriter().println();
			fColumn = 0;
		}*/
	}
	
	protected void printDefects(Enumeration booBoos,int count,String type){
		if(count == 0)return;
		if(count == 1)
			getWriter().println("There was " + count + " " + type + ":");
		else
			getWriter().println("There were " + count + " " + type + "s:");
		for(int i = 1; booBoos.hasMoreElements();i++){
			printDefect((TestFailure)booBoos.nextElement(),i);
		}
	}
	
	protected void printDefect(TestFailure booBoo, int count){
		printDefectHeader(booBoo,count);
		printDefectTrace(booBoo);//与源码不同
	}
	
	protected void printDefectHeader(TestFailure booBoo, int count) {
		// I feel like making this a println, then adding a line giving the throwable a chance to print something
		// before we get to the stack trace.
		getWriter().print(count + ") " + booBoo.failedTest());
	}
	
	protected void printDefectTrace(TestFailure booBoo){
		getWriter().print(booBoo.trace());
	}
	
	protected void printFooter(TestResult result) {
		if (result.wasSuccessful()) {
			getWriter().println();
			getWriter().print("JUnit精简版，纯属学习娱乐专用");
			getWriter().println();
			getWriter().print("OK");
			getWriter().println (" (" + result.runCount() + " test" + (result.runCount() == 1 ? "": "s") + ")");
			

		} else {
			getWriter().println();
			getWriter().println("FAILURES!!!");
			getWriter().println("Tests run: "+result.runCount()+ 
				         ",  Failures: "+result.failureCount()+
				         ",  Errors: "+result.errorCount());
		}
	    getWriter().println();
	}
	
}

