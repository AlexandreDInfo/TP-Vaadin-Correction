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

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@SuppressWarnings({ "deprecation", "serial" })
@Theme("mytheme")
public class MyUI extends UI {
    
    private CustomerService service = CustomerService.getInstance();
    private Grid<Customer> grid = new Grid<>(Customer.class);
    private TextField filterText = new TextField();
    private CustomerForm form = new CustomerForm(this);
    private Customer selectedCustomer = null;
    private Button editCustomerButton = new Button("Edit customer");
    private Button deleteCustomerButton = new Button("Delete customer");

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        filterText.setPlaceholder("filter by name...");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);

        Button clearFilterTextBtn = new Button(FontAwesome.TIMES);
        clearFilterTextBtn.setDescription("Clear the current filter");
        clearFilterTextBtn.addClickListener(e -> filterText.clear());

        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilterTextBtn);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        grid.setColumns("firstName", "lastName", "birthDate", "email");

        HorizontalLayout main = new HorizontalLayout(grid, form);
        main.setSizeFull();
        grid.setSizeFull();
        main.setExpandRatio(grid, 1);
        
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
        
        HorizontalLayout bottomButtons = new HorizontalLayout(newCustomerButton, editCustomerButton, deleteCustomerButton);
        
        layout.addComponents(filtering, main, bottomButtons);

        // fetch list of Customers from service and assign it to Grid
        updateList();

        setContent(layout);

        form.setVisible(false);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() == null) {
                form.setVisible(false);
            } else {
            	selectedCustomer = event.getValue();
            	editCustomerButton.setEnabled(true);
            	deleteCustomerButton.setEnabled(true);
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
