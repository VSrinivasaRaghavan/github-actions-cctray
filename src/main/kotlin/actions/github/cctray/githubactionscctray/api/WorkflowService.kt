package actions.github.cctray.githubactionscctray.api

import actions.github.cctray.githubactionscctray.model.ListWorkflowRuns
import actions.github.cctray.githubactionscctray.model.Projects
import actions.github.cctray.githubactionscctray.model.WorkflowRun
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.util.*
import javax.xml.datatype.DatatypeFactory


@Service
class WorkflowService(
    @Value("\${github.actions.fgpat}") private val token: String,
    @Value("\${github.repo.owner.name}") private val repoOwnerName: String,
    @Value("\${github.repo.name}") private val repoName: String,
    private val restTemplate: RestTemplate
) {
    fun getAllWorkflowLatestRuns(): Result<Projects> {
        val headers = HttpHeaders()
        headers.setBearerAuth(token)

        val entity = HttpEntity("body", headers)
        val response = restTemplate.exchange<ListWorkflowRuns>(
            "https://api.github.com/repos/${repoOwnerName}/${repoName}/actions/runs",
            HttpMethod.GET,
            entity
        )
        val listOfWorkflowRuns = response.body
        if (response.statusCode != HttpStatus.OK || listOfWorkflowRuns == null)
            return Result.failure(RestClientException("Couldn't connect to Github"))

        val projects = Projects()

        val projectList = listOfWorkflowRuns.workflowRuns
            .distinctBy { it.name }
            .map {
                mapToProject(it)
            }
        projects.project.addAll(projectList)

        return Result.success(projects)
    }

    private fun mapToProject(workflowRun: WorkflowRun): Projects.Project {
        val project = Projects.Project()
        project.activity = when (workflowRun.status) {
            "queued" -> Activity.BUILDING.value
            "in_progress" -> Activity.BUILDING.value
            else -> Activity.SLEEPING.value
        }

        project.lastBuildStatus = when (workflowRun.conclusion) {
            "success" -> LastBuildStatus.SUCCESS.value
            "failure" -> LastBuildStatus.FAILURE.value
            else -> LastBuildStatus.UNKNOWN.value
        }
        val gregorianCalendar = GregorianCalendar()
        gregorianCalendar.time = workflowRun.updatedAt
        project.lastBuildTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar)
        project.name = workflowRun.name
        project.webUrl = workflowRun.htmlUrl

        return project
    }
}

enum class Activity(val value: String) {
    SLEEPING("Sleeping"),
    BUILDING("Building")
}

enum class LastBuildStatus(val value: String) {
    FAILURE("Failure"),
    SUCCESS("Success"),
    UNKNOWN("Unknown")
}
