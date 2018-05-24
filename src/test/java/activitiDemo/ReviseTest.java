package activitiDemo;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

public class ReviseTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	@Before
	public void before() {
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000").setJdbcUsername("sa").setJdbcPassword("")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		processEngine = cfg.buildProcessEngine();
		String pName = processEngine.getName();
		String ver = ProcessEngine.VERSION;
		System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");
	}

	@Test
	public void deploymentProcessDefiniton_inputStream() throws ParseException {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment().addClasspathResource("diagrams/ReviseProcess.bpmn")
				.deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println("Found process definition [" + processDefinition.getName() + "] with id ["
				+ processDefinition.getId() + "]");

		RuntimeService runtimeService = processEngine.getRuntimeService();
		HashMap variables = new HashMap();
		Revise revise = new Revise();
		revise.setLargeare("largeare");
		revise.setMajordomo("majordomo");
		revise.setManager("manager");
		variables.put("revise", revise);
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("reviseProcess", variables);
		System.out.println("Onboarding process started with process instance id ["
				+ processInstance.getProcessInstanceId() + "] key [" + processInstance.getProcessDefinitionKey() + "]");

		TaskService taskService = processEngine.getTaskService();
		List<Task> managerTaskList = taskService.createTaskQuery()//
				.taskAssignee("manager")
				.list();
		for (Task task : managerTaskList) {
			System.out.println("id=" + task.getId());
			System.out.println("name=" + task.getName());
			System.out.println("assinee=" + task.getAssignee());
			System.out.println("createTime=" + task.getCreateTime());
			System.out.println("executionId=" + task.getExecutionId());
			taskService.complete(task.getId());
		}
		
		List<Task> majordomoTaskList = taskService.createTaskQuery()//
				.taskAssignee("majordomo")
				.list();
		for (Task task : majordomoTaskList) {
			System.out.println("id=" + task.getId());
			System.out.println("name=" + task.getName());
			System.out.println("assinee=" + task.getAssignee());
			System.out.println("createTime=" + task.getCreateTime());
			System.out.println("executionId=" + task.getExecutionId());
			taskService.complete(task.getId());
		}
		
		System.out.println("==============end===============");
	}
}
