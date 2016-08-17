package pl.com.bottega.documentmanagement.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static junit.framework.TestCase.*;

/**
 * Created by paulina.pislewicz on 2016-07-31.
 */

@RunWith(MockitoJUnitRunner.class)
public class DocumentTest {
    private static final long EPS = 2 * 1000L;
    @Mock
    private DocumentNumber anyNumber;
    @Mock
    private Employee anyEmployee;

    @Mock
    private Employee anyVerificator;

    private String anyContent = "test content";
    private String anyTitle = "test title";
    private String modifiedContent = "modified test content";
    private String modifiedTitle = "modified test title";

    @Test
    public void shouldCreateDocumentWithInitalState() {

        //when
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);

        //then
        assertEquals(anyNumber, document.number());
        assertEquals(anyContent, document.content());
        assertEquals(anyTitle, document.title());
        assertEquals(anyEmployee, document.creator());
        assertFalse(document.deleted());
        assertEquals(DocumentStatus.DRAFT, document.documentStatus());
    }

    @Test
    public void shouldVerifyDocument() {
        //given
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);
        //when
        document.verify(anyEmployee);
        //then
        assertEquals(DocumentStatus.VERIFIED, document.documentStatus());
        assertNotNull(document.verificatedAt());
        assertEquals(anyEmployee, document.verificator());
        assertTrue(Math.abs(new Date().getTime() - document.verificatedAt().getTime()) < EPS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRequiredVerificator() {
        //given
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);

        //when
        document.verify(null);
    }

    @Test
    public void shouldRequiredVerificatorOtherWay() {
        //given
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);

        //when
        try {
            document.verify(null);
        } catch (IllegalArgumentException ex) {
            return;
        }
        fail("IllegalArgumentExceptionExpected");
    }

    @Test
    public void shouldEditDocument(){
        //given
        Document document = new Document(anyNumber, anyContent, anyTitle, anyEmployee);
        //when
        document.change(modifiedTitle, modifiedContent);
        //then
        assertEquals(modifiedContent, document.content());
        assertEquals(modifiedTitle, document.title());
    }

    @Test
    public void shouldDeleteDocument(){
        //given
        Document document = new Document(anyNumber,anyContent, anyTitle, anyEmployee);
        //when
        document.delete();
        //then
        assertTrue(document.deleted());

    }
}