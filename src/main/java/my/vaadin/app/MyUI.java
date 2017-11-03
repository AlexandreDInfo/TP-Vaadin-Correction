package my.vaadin.app;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings({ "deprecation", "serial" })
@Theme("mytheme")
public class MyUI extends UI {
    
    private CustomerService service = CustomerService.getInstance();
    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField filterText = new TextField();
    private Customer selectedCustomer = null;
    private Button editCustomerButton = new Button("Edit customer");
    private Button deleteCustomerButton = new Button("Delete customer");
    private Button enableButton = new Button("Enable Modification");
    private Button disableButton = new Button("Disable Modification");

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();
        
        // filter
        filterText.setPlaceholder("filter by name...");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        
        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        
        // Main grid
        grid.setColumns("firstName", "lastName", "birthDate", "email", "modifiable");

        HorizontalLayout main = new HorizontalLayout(grid);
        main.setSizeFull();
        grid.setSizeFull();
        
        
        // Bottom buttons
        Button newCustomerButton = new Button("Add new customer");
        newCustomerButton.addClickListener(e -> {
        	grid.asSingleSelect().clear();
        	editCustomerButton.setEnabled(false);
        	deleteCustomerButton.setEnabled(false);
        	addWindow(new SaveWindow(this, new Customer()));
        });
        
        editCustomerButton.setEnabled(false);
        editCustomerButton.addClickListener(e -> {
        	if(selectedCustomer != null) {
        		addWindow(new SaveWindow(this, selectedCustomer));
        	}
        });
        
        deleteCustomerButton.setEnabled(false);
        deleteCustomerButton.addClickListener(e -> {
        	if(selectedCustomer != null) {        		
        		addWindow(new DeleteWindow(this, selectedCustomer));
        	}
        });
        
        enableButton.setVisible(false);
        enableButton.addClickListener(e -> {
        	selectedCustomer.setModifiable(true);
        	grid.asSingleSelect().clear();
        });
        
        disableButton.setVisible(false);
        disableButton.addClickListener(e -> {
        	selectedCustomer.setModifiable(false);
        	grid.asSingleSelect().clear();
        });
        
        HorizontalLayout bottomButtons = new HorizontalLayout(newCustomerButton, editCustomerButton, deleteCustomerButton, enableButton, disableButton);
        
        layout.addComponents(filtering, main, bottomButtons);

        // fetch list of Customers from service and assign it to Grid
        updateList();

        setContent(layout);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
            	selectedCustomer = event.getValue();
            	editCustomerButton.setEnabled(true);
            	deleteCustomerButton.setEnabled(true);
            	if(event.getValue().isModifiable()){
            		enableButton.setVisible(false);
            		disableButton.setVisible(true);
            		editCustomerButton.setEnabled(true);
            		deleteCustomerButton.setEnabled(true);
            	}else{
            		enableButton.setVisible(true);
            		disableButton.setVisible(false);
            		editCustomerButton.setEnabled(false);
            		deleteCustomerButton.setEnabled(false);
            	}
            }
            else{
            	selectedCustomer = null;
            	enableButton.setVisible(false);
            	disableButton.setVisible(false);
            	editCustomerButton.setEnabled(false);
            	deleteCustomerButton.setEnabled(false);
            }
        });
        
    }

    public void updateList() {
        List<Customer> customers = service.findAll(filterText.getValue());
        grid.setItems(customers);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
