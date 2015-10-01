package br.com.mockito;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Exemplos:
 * http://site.mockito.org/mockito/docs/current/org/mockito/Mockito.html
 * Quatidade de exemplos: 32
 * 
 * @author franklinoliveira
 *
 */
public class MockitoExemplos {

	@Mock
	List mockedList;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Let's verify some behaviour!
	 */
	@Test
	public void mock1() {

		// using mock object
		mockedList.add("one");
		mockedList.clear();

		// verification
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}

	/**
	 * How about some stubbing?
	 */
	@Test(expected = RuntimeException.class)
	public void mock2() {
		// You can mock concrete classes, not only interfaces
		LinkedList mockedList = mock(LinkedList.class);

		// stubbing
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());

		// following prints "first"
		System.out.println(mockedList.get(0));

		// following throws runtime exception
		System.out.println(mockedList.get(1));

		// following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));

		// Although it is possible to verify a stubbed invocation, usually it's
		// just redundant
		// If your code cares what get(0) returns then something else breaks
		// (often before even verify() gets executed).
		// If your code doesn't care what get(0) returns then it should not be
		// stubbed. Not convinced? See here.
		verify(mockedList).get(0);
	}

	/**
	 * Argument matchers
	 */
	@Test
	public void mock3() {
		// stubbing using built-in anyInt() argument matcher
		when(mockedList.get(anyInt())).thenReturn("element");

		// stubbing using custom matcher:
		when(mockedList.contains(anyString())).thenReturn(false);

		// following prints "element"
		System.out.println(mockedList.get(999));

		// following prints "false"
		System.out.println(mockedList.contains("string"));

		// you can also verify using an argument matcher
		verify(mockedList).get(anyInt());
	}

	/**
	 * Verifying exact number of invocations / at least x / never
	 */
	@Test
	public void mock4() {
		// using mock
		mockedList.add("once");

		mockedList.add("twice");
		mockedList.add("twice");

		mockedList.add("three times");
		mockedList.add("three times");
		mockedList.add("three times");

		// following two verifications work exactly the same - times(1) is used
		// by default
		verify(mockedList).add("once");
		verify(mockedList, times(1)).add("once");

		// exact number of invocations verification
		verify(mockedList, times(2)).add("twice");
		verify(mockedList, times(3)).add("three times");

		// verification using never(). never() is an alias to times(0)
		verify(mockedList, never()).add("never happened");

		// verification using atLeast()/atMost()
		verify(mockedList, atLeastOnce()).add("three times");
		verify(mockedList, atLeast(2)).add("three times");
		verify(mockedList, atMost(5)).add("three times");

		// times(1) is the default. Therefore using times(1) explicitly can be
		// omitted.
	}

	/**
	 * Stubbing void methods with exceptions
	 */
	@Test(expected = RuntimeException.class)
	public void mock5() {
		doThrow(new RuntimeException()).when(mockedList).clear();

		// following throws RuntimeException:
		mockedList.clear();
	}

	/**
	 * Verification in order.
	 */
	@Test
	public void mock6() {
		// A. Single mock whose methods must be invoked in a particular order
		List singleMock = mock(List.class);

		// using a single mock
		singleMock.add("was added first");
		singleMock.add("was added second");

		// create an inOrder verifier for a single mock
		InOrder inOrder = inOrder(singleMock);

		// following will make sure that add is first called with
		// "was added first, then with "was added second"
		inOrder.verify(singleMock).add("was added first");
		inOrder.verify(singleMock).add("was added second");

		// B. Multiple mocks that must be used in a particular order
		List firstMock = mock(List.class);
		List secondMock = mock(List.class);

		// using mocks
		firstMock.add("was called first");
		secondMock.add("was called second");

		// create inOrder object passing any mocks that need to be verified in
		// order
		InOrder inOrder2 = inOrder(firstMock, secondMock);

		// following will make sure that firstMock was called before secondMock
		inOrder2.verify(firstMock).add("was called first");
		inOrder2.verify(secondMock).add("was called second");

		// Oh, and A + B can be mixed together at will

		// Verification in order is flexible - you don't have to verify all
		// interactions one-by-one but only those that you are interested in
		// testing in order.
		// Also, you can create InOrder object passing only mocks that are
		// relevant for in-order verification.
	}

	/**
	 * Making sure interaction(s) never happened on mock
	 */
	@Test
	public void mock7() {
		List mockOne = mock(List.class);
		List mockTwo = mock(List.class);
		List mockThree = mock(List.class);

		// using mocks - only mockOne is interacted
		mockOne.add("one");

		// ordinary verification
		verify(mockOne).add("one");

		// verify that method was never called on a mock
		verify(mockOne, never()).add("two");

		// verify that other mocks were not interacted
		verifyZeroInteractions(mockTwo, mockThree);
	}

	/**
	 * Finding redundant invocations
	 */
	@Test
	public void mock8() {
		// using mocks
		mockedList.add("one");
		mockedList.add("two");

		verify(mockedList).add("one");

		// following verification will fail
		verifyNoMoreInteractions(mockedList);
	}

	/**
	 * Shorthand for mocks creation - @Mock annotation
	 */
	@Test
	public void mock9() {

	}

	/**
	 * Stubbing consecutive calls (iterator-style stubbing)
	 */
	@Test
	public void mock10() {
		/*
		 * when(mock.someMethod("some arg")).thenThrow(new
		 * RuntimeException()).thenReturn("foo");
		 * 
		 * // First call: throws runtime exception: mock.someMethod("some arg");
		 * 
		 * // Second call: prints "foo"
		 * System.out.println(mock.someMethod("some arg"));
		 * 
		 * // Any consecutive call: prints "foo" as well (last stubbing wins).
		 * System.out.println(mock.someMethod("some arg"));
		 * 
		 * Alternative, shorter version of consecutive stubbing:
		 * when(mock.someMethod("some arg")).thenReturn("one", "two", "three");
		 */
	}

	/**
	 * Stubbing with callbacks
	 */
	@Test
	public void mock11() {
		/*
		 * when(mock.someMethod(anyString())).thenAnswer(new Answer() { Object
		 * answer(InvocationOnMock invocation) { Object[] args =
		 * invocation.getArguments(); Object mock = invocation.getMock(); return
		 * "called with arguments: " + args; } });
		 * 
		 * // Following prints "called with arguments: foo"
		 * System.out.println(mock.someMethod("foo"));
		 */
	}

	/**
	 * doReturn()|doThrow()| doAnswer()|doNothing()|doCallRealMethod() family of
	 * methods
	 */
	@Test
	public void mock12() {
		doThrow(new RuntimeException()).when(mockedList).clear();

		// following throws RuntimeException:
		mockedList.clear();
	}

	/**
	 * Spying on real objects
	 */
	@Test
	public void mock13() {
		List list = new LinkedList();
		List spy = spy(list);

		// optionally, you can stub out some methods:
		when(spy.size()).thenReturn(100);

		// using the spy calls *real* methods
		spy.add("one");
		spy.add("two");

		// prints "one" - the first element of a list
		// Observação: mockList.get(0) retornaria null.
		System.out.println(spy.get(0));

		// size() method was stubbed - 100 is printed
		System.out.println(spy.size());

		// optionally, you can verify
		verify(spy).add("one");
		verify(spy).add("two");

		/**
		 * Important gotcha on spying real objects!
		 */
		List list2 = new LinkedList();
		List spy2 = spy(list2);

		// Impossible: real method is called so spy.get(0) throws
		// IndexOutOfBoundsException (the list is yet empty)
		when(spy2.get(0)).thenReturn("foo");

		// You have to use doReturn() for stubbing
		doReturn("foo").when(spy2).get(0);

	}

	/**
	 * Changing default return values of unstubbed invocations (Since 1.7)
	 */
	@Test
	public void mock14() {
		/*
		 * Foo mock = mock(Foo.class, Mockito.RETURNS_SMART_NULLS); Foo mockTwo
		 * = mock(Foo.class, new YourOwnAnswer());
		 */
	}

	/**
	 * Capturing arguments for further assertions (Since 1.8.0)
	 */
	@Test
	public void mock15() {
		/*
		 * ArgumentCaptor<Person> argument =
		 * ArgumentCaptor.forClass(Person.class);
		 * verify(mock).doSomething(argument.capture()); assertEquals("John",
		 * argument.getValue().getName());
		 */
	}

	/**
	 * Real partial mocks (Since 1.8.0)
	 */
	@Test
	public void mock16() {
		/*
		 * // you can create partial mock with spy() method: List list = spy(new
		 * LinkedList());
		 * 
		 * // you can enable partial mock capabilities selectively on mocks: Foo
		 * mock = mock(Foo.class); // Be sure the real implementation is 'safe'.
		 * // If real implementation throws exceptions or depends on specific
		 * state // of the object then you're in trouble.
		 * when(mock.someMethod()).thenCallRealMethod();
		 */
	}

	/**
	 * Resetting mocks (Since 1.8.0)
	 */
	@Test
	public void mock17() {
		List mock = mock(List.class);
		when(mock.size()).thenReturn(10);
		mock.add(1);

		reset(mock);
		// at this point the mock forgot any interactions & stubbing
	}

	/**
	 * Troubleshooting & validating framework usage (Since 1.8.0)
	 */
	@Test
	public void mock18() {
		/*
		 * First of all, in case of any trouble, I encourage you to read the
		 * Mockito FAQ: http://code.google.com/p/mockito/wiki/FAQ In case of
		 * questions you may also post to mockito mailing list:
		 * http://groups.google.com/group/mockito
		 * 
		 * Next, you should know that Mockito validates if you use it correctly
		 * all the time. However, there's a gotcha so please read the javadoc
		 * for validateMockitoUsage()
		 */
	}

	/**
	 * Aliases for behavior driven development (Since 1.8.0)
	 */
	@Test
	public void mock19() {
		/*
		 * import static org.mockito.BDDMockito.*;
		 * 
		 * Seller seller = mock(Seller.class); Shop shop = new Shop(seller);
		 * 
		 * public void shouldBuyBread() throws Exception { //given
		 * given(seller.askForBread()).willReturn(new Bread());
		 * 
		 * //when Goods goods = shop.buyBread();
		 * 
		 * //then assertThat(goods, containBread()); }
		 */
	}

	/**
	 * Serializable mocks (Since 1.8.1)
	 */
	@Test
	public void mock20() {
		// To create serializable mock use MockSettings.serializable():
		List serializableMock = mock(List.class, withSettings().serializable());

		/*
		 * List<Object> list = new ArrayList<Object>(); List<Object> spy = mock(
		 * ArrayList.class, withSettings().spiedInstance(list)
		 * .defaultAnswer(CALLS_REAL_METHODS).serializable());
		 */
	}

	/**
	 * New annotations: @Captor, @Spy, @InjectMocks (Since 1.8.3)
	 */
	@Test
	public void mock21() {
		/*
		 * @Captor simplifies creation of ArgumentCaptor - useful when the
		 * argument to capture is a nasty generic class and you want to avoid
		 * compiler warnings
		 * 
		 * @Spy - you can use it instead spy(Object).
		 * 
		 * @InjectMocks - injects mock or spy fields into tested object
		 * automatically.
		 */
	}

	/**
	 * Verification with timeout (Since 1.8.5)
	 */
	@Test
	public void mock22() {
		/*
		List mock = mock(List.class);
		
		// passes when someMethod() is called within given time span
		verify(mock, timeout(100)).someMethod();
		// above is an alias to:
		verify(mock, timeout(100).times(1)).someMethod();

		// passes when someMethod() is called *exactly* 2 times within given
		// time span
		verify(mock, timeout(100).times(2)).someMethod();

		// passes when someMethod() is called *at least* 2 times within given
		// time span
		verify(mock, timeout(100).atLeast(2)).someMethod();

		// verifies someMethod() within given time span using given verification
		// mode
		// useful only if you have your own custom verification modes.
		verify(mock, new Timeout(100, yourOwnVerificationMode)).someMethod();
		*/
	}
	
	/**
	 * Automatic instantiation of @Spies, @InjectMocks and constructor injection goodness (Since 1.9.0)
	 */
	@Test
	public void mock23() {
		/*
		//instead:
		@Spy BeerDrinker drinker = new BeerDrinker();
		//you can write:
		@Spy BeerDrinker drinker;

		//same applies to @InjectMocks annotation:
		@InjectMocks LocalPub;
		*/
	}
	
	/**
	 * One-liner stubs (Since 1.9.0)
	 */
	@Test
	public void mock24() {
		/*
		 * Mockito will now allow you to create mocks when stubbing. Basically,
		 * it allows to create a stub in one line of code. This can be helpful
		 * to keep test code clean. For example, some boring stub can be created
		 * & stubbed at field initialization in a test:
		 */
		/*
		public class CarTest {
			   Car boringStubbedCar = when(mock(Car.class).shiftGear()).thenThrow(EngineNotStarted.class).getMock();

			   @Test public void should... {}
		*/
	}
	
	/**
	 * Verification ignoring stubs (Since 1.9.0)
	 */
	@Test
	public void mock25() {
		/*
		verify(mock).foo();
		verify(mockTwo).bar();

		//ignores all stubbed methods:
		verifyNoMoreInvocations(ignoreStubs(mock, mockTwo));

		//creates InOrder that will ignore stubbed
		InOrder inOrder = inOrder(ignoreStubs(mock, mockTwo));
		inOrder.verify(mock).foo();
		inOrder.verify(mockTwo).bar();
		inOrder.verifyNoMoreInteractions();
		*/
	}
	
	/**
	 * Mocking details (Since 1.9.5)
	 */
	@Test
	public void mock26() {
		//To identify whether a particular object is a mock or a spy:
		/*
	    Mockito.mockingDetails(someObject).isMock();
	    Mockito.mockingDetails(someObject).isSpy();
	    */
	}
	
	/**
	 * Delegate calls to real instance (Since 1.9.5)
	 */
	@Test
	public void mock27() {
		/*
		 * Useful for spies or partial mocks of objects that are difficult to
		 * mock or spy using the usual spy API. Since Mockito 1.10.11, the
		 * delegate may or may not be of the same type as the mock. If the type
		 * is different, a matching method needs to be found on delegate type
		 * otherwise an exception is thrown. Possible use cases for this
		 * feature:
		 * 
		 * Final classes but with an interface Already custom proxied object
		 * Special objects with a finalize method, i.e. to avoid executing it 2
		 * times The difference with the regular spy:
		 * 
		 * The regular spy (spy(Object)) contains all state from the spied
		 * instance and the methods are invoked on the spy. The spied instance
		 * is only used at mock creation to copy the state from. If you call a
		 * method on a regular spy and it internally calls other methods on this
		 * spy, those calls are remembered for verifications, and they can be
		 * effectively stubbed. The mock that delegates simply delegates all
		 * methods to the delegate. The delegate is used all the time as methods
		 * are delegated onto it. If you call a method on a mock that delegates
		 * and it internally calls other methods on this mock, those calls are
		 * not remembered for verifications, stubbing does not have effect on
		 * them, too. Mock that delegates is less powerful than the regular spy
		 * but it is useful when the regular spy cannot be created.
		 */
	}
	
	/**
	 * MockMaker API (Since 1.9.5)
	 */
	@Test
	public void mock28() {
		/*
		 * Driven by requirements and patches from Google Android guys Mockito
		 * now offers an extension point that allows replacing the proxy
		 * generation engine. By default, Mockito uses cglib to create dynamic
		 * proxies.
		 * 
		 * The extension point is for advanced users that want to extend
		 * Mockito. For example, it is now possible to use Mockito for Android
		 * testing with a help of dexmaker.
		 * 
		 * For more details, motivations and examples please refer to the docs
		 * for MockMaker.
		 */
	}
	
	/**
	 * (new) BDD style verification (Since 1.10.0)
	 */
	@Test
	public void mock29() {
		/*
		given(dog.bark()).willReturn(2);

		// when
		...

		then(person).should(times(2)).ride(bike);
		*/
	}
	
	/**
	 * (new) Spying or mocking abstract classes (Since 1.10.12)
	 */
	@Test
	public void mock30() {
		/*
		//convenience API, new overloaded spy() method:
		SomeAbstract spy = spy(SomeAbstract.class);

		//Robust API, via settings builder:
		OtherAbstract spy = mock(OtherAbstract.class, withSettings()
		    .useConstructor().defaultAnswer(CALLS_REAL_METHODS));

		//Mocking a non-static inner abstract class:
		InnerAbstract spy = mock(InnerAbstract.class, withSettings()
		    .useConstructor().outerInstance(outerInstance).defaultAnswer(CALLS_REAL_METHODS));
		*/
	}
	
	/**
	 * (new) Mockito mocks can be serialized / deserialized across classloaders (Since 1.10.0)
	 */
	@Test
	public void mock31() {
		/*
		// use regular serialization
		mock(Book.class, withSettings().serializable());

		// use serialization across classloaders
		mock(Book.class, withSettings().serializable(ACROSS_CLASSLOADERS));
		*/
	}
	
	/**
	 * Better generic support with deep stubs (Since 1.10.0)
	 */
	@Test
	public void mock32() {
		/*
		class Lines extends List<Line> {
		    // ...
		}

		lines = mock(Lines.class, RETURNS_DEEP_STUBS);

		// Now Mockito understand this is not an Object but a Line
		Line line = lines.iterator().next();
		*/
	}
	
	/**
	 * (new) Mockito JUnit rule (Since 1.10.17)
	 */
	@Test
	public void mock33() {
		/*
		@RunWith(YetAnotherRunner.class)
		public class TheTest {
		    @Rule public MockitoRule mockito = MockitoJUnit.rule();
		    // ...
		}
		*/
	}
	
	/**
	 * (new) Switch on or off plugins (Since 1.10.15)
	 */
	@Test
	public void mock34() {
		/*
		 * An incubating feature made it's way in mockito that will allow to
		 * toggle a mockito-plugin. More information here PluginSwitch.
		 */
	}
	
	/**
	 * Custom verification failure message (Since 2.0.0)
	 */
	@Test
	public void mock35() {
		/*
		// will print a custom message on verification failure 
		verify(mock, description("This will print on failure")).someMethod();
		 
		// will work with any verification mode 
		verify(mock, times(2).description("someMethod should be called twice")).someMethod();
		*/
	}
	
	
	

}
