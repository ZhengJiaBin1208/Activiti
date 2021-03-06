package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.bpmn.model.UserTask;
import org.junit.Test;

public class ScopedConverterTest extends AbstractConverterTest {

  @Test
  public void convertXMLToModel() throws Exception {
    BpmnModel bpmnModel = readXMLFile();
    validateModel(bpmnModel);
  }

  @Test
  public void convertModelToXML() throws Exception {
    BpmnModel bpmnModel = readXMLFile();
    BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
    validateModel(parsedModel);
    deployProcess(parsedModel);
  }

  protected String getResource() {
    return "scopedmodel.bpmn";
  }

  private void validateModel(BpmnModel model) {
    FlowElement flowElement = model.getMainProcess().getFlowElement("outerSubProcess");
    assertThat(flowElement).isNotNull();
    assertThat(flowElement).isInstanceOf(SubProcess.class);
    assertThat(flowElement.getId()).isEqualTo("outerSubProcess");
    SubProcess outerSubProcess = (SubProcess) flowElement;
    List<BoundaryEvent> eventList = outerSubProcess.getBoundaryEvents();
    assertThat(eventList).hasSize(1);
    BoundaryEvent boundaryEvent = eventList.get(0);
    assertThat(boundaryEvent.getId()).isEqualTo("outerBoundaryEvent");

    FlowElement subElement = outerSubProcess.getFlowElement("innerSubProcess");
    assertThat(subElement).isNotNull();
    assertThat(subElement).isInstanceOf(SubProcess.class);
    assertThat(subElement.getId()).isEqualTo("innerSubProcess");
    SubProcess innerSubProcess = (SubProcess) subElement;
    eventList = innerSubProcess.getBoundaryEvents();
    assertThat(eventList).hasSize(1);
    boundaryEvent = eventList.get(0);
    assertThat(boundaryEvent.getId()).isEqualTo("innerBoundaryEvent");

    FlowElement taskElement = innerSubProcess.getFlowElement("usertask");
    assertThat(taskElement).isNotNull();
    assertThat(taskElement).isInstanceOf(UserTask.class);
    UserTask userTask = (UserTask) taskElement;
    assertThat(userTask.getId()).isEqualTo("usertask");
    eventList = userTask.getBoundaryEvents();
    assertThat(eventList).hasSize(1);
    boundaryEvent = eventList.get(0);
    assertThat(boundaryEvent.getId()).isEqualTo("taskBoundaryEvent");
  }
}
