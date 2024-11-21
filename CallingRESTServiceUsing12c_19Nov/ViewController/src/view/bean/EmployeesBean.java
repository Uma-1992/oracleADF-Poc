package view.bean;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.Serializable;

import java.math.BigDecimal;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.binding.BindingContainer;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaManager;
import oracle.jbo.ViewObject;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class EmployeesBean  {
    //implements Serializable
     //private static final long serialVersionUID = 1L;
    private Integer managerId;
    private List<TableBean> empTableBean = new ArrayList<TableBean>();

    public EmployeesBean() {       
        super();
        
    }

    public String invokeManagerDetails() {
        Integer id = this.getManagerId();
        BindingContainer bindingContainer = BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding employeesVOIterator = (DCIteratorBinding) bindingContainer.get("EmployeesView1Iterator");
        ViewObject empVO = employeesVOIterator.getViewObject();
        ViewCriteriaManager criteriaManager = empVO.getViewCriteriaManager();
        ViewCriteria viewCriteria = criteriaManager.getViewCriteria("EmployeesViewCriteria");
        empVO.setNamedWhereClauseParam("P_Manager", id);
        empVO.applyViewCriteria(viewCriteria);
        empVO.executeQuery();
        RowSetIterator rs = empVO.createRowSetIterator(null);
        System.out.println("Estimated Row Count :- " + empVO.getEstimatedRowCount());
      //  List<TableBean> beans = this.getEmpTableBean();
        List<TableBean> beans = this.getEmpTableBean();
        System.out.println("TableBeanList------------"+beans);
        //TableBean bean = new TableBean();
        //System.out.println("TableBean"+bean);
        System.out.println("Entering while loop");
        TableBean bean = new TableBean();
        while (rs.hasNext()) {            
            Row row = rs.next();
            
            System.out.println("EmployeeId is: ------- "+row.getAttribute("EmployeeId"));
            bean.setEmpId((Integer) row.getAttribute("EmployeeId"));
            
            System.out.println("FirstName is: ------------- "+row.getAttribute("FirstName"));
            bean.setFname((String) row.getAttribute("FirstName"));
            
            System.out.println("LastName: ---------- "+row.getAttribute("LastName"));
            bean.setLname((String) row.getAttribute("LastName"));
            
            System.out.println("Salary is: ------------- "+row.getAttribute("Salary"));
            bean.setSalary((BigDecimal) row.getAttribute("Salary"));
            
            System.out.println("Email: --------------- "+row.getAttribute("Email"));
            bean.setEmail((String) row.getAttribute("Email"));

           beans.add(bean);
        }
        System.out.println("Exit While loop");
       
        System.out.println("Closed Rowset Iterator....");
       rs.closeRowSetIterator();
        try {
            String url =
                "http://127.0.0.1:7101/CallingRESTServiceUsing12c_19Nov-RESTWebService-context-root/rest/1/emps?q=ManagerId="+id;
            System.out.println("Consolid URL:" + url);
            System.out.println("Consolid URL....." + url);
            System.out.println(url);
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("mapper");
            String jsonInString = mapper.writeValueAsString(this.getEmpTableBean());
            System.out.println("Request JSON:" + jsonInString);
            System.out.println("Request JSON:" + jsonInString);
            URL object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            System.out.println("CON : " + con);

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            con.getOutputStream().write(jsonInString.getBytes("UTF-8"));
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    //sb.append(line + "\n");
                    sb.append(line).append("\n");
//                    System.out.println("SB : " + sb.toString());
//                    System.out.println("SB : " + sb.toString());

                }
                br.close();
                System.out.println("SB : " + sb.toString());
                List<TableBean> empList = mapper.readValue(sb.toString(), new TypeReference<List<TableBean>>(){});
                this.setEmpTableBean(empList);
                System.out.println("SB : " + sb.toString());
            } else {

                System.out.println("Request failed with response code: " + HttpResult);
            }
            con.disconnect();
        } catch (Exception e) {

            // TODO: Add catch code
            e.printStackTrace();
            FacesMessage message = new FacesMessage("Error while retreving employees Detals!");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, message);
        }
System.out.println("Employees List--++"+empTableBean);
        return "";
    }

//    public String convertToJSON(Object object) {
//        String json = null;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            json = objectMapper.writeValueAsString(object);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return json;
//    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getManagerId() {
        return managerId;
    }


    public void setEmpTableBean(List<TableBean> empTableBean) {
        this.empTableBean = empTableBean;
    }

    public List<TableBean> getEmpTableBean() {
        return empTableBean;
    }
}
