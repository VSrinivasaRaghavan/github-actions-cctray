package actions.github.cctray.githubactionscctray.api

import com.sun.xml.txw2.output.IndentingXMLStreamWriter
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.io.StringWriter
import javax.xml.bind.JAXBContext
import javax.xml.stream.XMLOutputFactory

@Controller
@RequestMapping("/workflows")
class WorkflowController(val workflowService: WorkflowService) {

    @GetMapping
    fun workflowStatus(): ResponseEntity<String?> {
        val projectsResult = workflowService.getAllWorkflowLatestRuns()
        val projects = projectsResult.getOrThrow()

        val stringWriter = StringWriter()
        val xmlWriter = IndentingXMLStreamWriter(XMLOutputFactory.newDefaultFactory().createXMLStreamWriter(stringWriter))
        val jaxbContext = JAXBContext.newInstance("actions.github.cctray.githubactionscctray.model")
        val marshal = jaxbContext.createMarshaller()
        marshal.marshal(projects, xmlWriter)

        return ResponseEntity.status(200).body(stringWriter.toString())
    }
}
