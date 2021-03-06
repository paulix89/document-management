package pl.com.bottega.documentmanagement.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.documentmanagement.domain.*;
import pl.com.bottega.documentmanagement.domain.repositories.DocumentRepository;
import pl.com.bottega.documentmanagement.domain.repositories.EmployeeRepository;

import java.util.Collection;
import java.util.Set;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by paulina.pislewicz on 2016-08-08.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentFlowProcessTest {
    private String anyContent = "any Content";
    private String anyTitle = "any Title";
    private String modifiedContent = "modified Content";
    private String modifiedTitle = "modified Title";

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private UserManager userManager;
    @Mock
    DocumentNumberGenerator documentNumberGenerator;
    @Mock
    private DocumentFactory documentFactory;
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeFactory employeeFactory;

    @Mock
    private Set<EmployeeId> employeeIdsObligatedToRead;

    @Mock
    private Collection<Employee> employeeObligatedToRead;

    @Mock
    private Employee creator;
    @Mock
    private Employee publisher;
    @Mock
    private DocumentNumber freeDocumentNumber;
    @Mock
    private DocumentNumber occupiedDocumentNumber;
    @Mock
    private Document document;




    @Test
    public void shouldCreateDocument(){
        //given
        DocumentFlowProcess documentFlowProcess = new DocumentFlowProcess(documentRepository, documentFactory, userManager, employeeRepository);
        when(documentFactory.create(anyContent, anyTitle)).thenReturn(document);
        when(document.number()).thenReturn(freeDocumentNumber);

        //when
        DocumentNumber result = documentFlowProcess.create(anyTitle, anyContent);

        //then
        verify(documentRepository).save(document);
        assertEquals(result, freeDocumentNumber);
        assertEquals(userManager.currentEmployee(), document.creator());

    }


    @Test
    public void shouldFailedIfContentIsNull(){
        //given
        DocumentFlowProcess documentFlowProcess = new DocumentFlowProcess(documentRepository, documentFactory, userManager, employeeRepository);

        try{
            documentFlowProcess.create(anyTitle, null);

        } catch (NullPointerException ex){
            return;
        }
        fail("NullPointerExceptionExpected!");

    }

    @Test
    public void shouldFailedIfTitleIsNull(){
        //given
        DocumentFlowProcess documentFlowProcess = new DocumentFlowProcess(documentRepository, documentFactory, userManager, employeeRepository);

        try{
            documentFlowProcess.create(anyTitle, null);
            documentFlowProcess.create(null, anyContent);

        } catch (NullPointerException ex){
            return;
        }
        fail("NullPointerExceptionExpected!");

    }

    @Test
    public void shouldChangeDocument(){
        //given
        DocumentFlowProcess documentFlowProcess = new DocumentFlowProcess(documentRepository, documentFactory, userManager, employeeRepository);
        Document document = new Document(occupiedDocumentNumber, anyContent, anyTitle, creator);
        when(documentRepository.load(occupiedDocumentNumber)).thenReturn(document);

        //when
        documentFlowProcess.change(occupiedDocumentNumber, modifiedTitle, modifiedContent);
        //then
        verify(documentRepository).save(document);
        assertEquals(modifiedContent, document.content());
        assertEquals(modifiedTitle, document.title());
        assertNotEquals(anyContent, document.content());
    }

    @Test
    public void shouldPublishDocument(){
        //given
        DocumentFlowProcess documentFlowProcess = new DocumentFlowProcess(documentRepository, documentFactory, userManager, employeeRepository);
        Document document = new Document(occupiedDocumentNumber, anyContent, anyTitle, creator);
        when(documentRepository.load(occupiedDocumentNumber)).thenReturn(document);
        when(userManager.currentEmployee()).thenReturn(publisher);
        when(employeeRepository.findByEmployeeIds(employeeIdsObligatedToRead)).thenReturn(employeeObligatedToRead);

        //when
        documentFlowProcess.publish(occupiedDocumentNumber,employeeIdsObligatedToRead);

        //then
        assertEquals(DocumentStatus.PUBLISHED, document.documentStatus());
    }


}
