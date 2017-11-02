package my.vaadin.app;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class SaveWindow extends Window {

	private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField email = new TextField("Email");
    private DateField birthdate = new DateField("Birthday");
    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel", event -> close());

    private CustomerService service = CustomerService.getInstance();
    private Binder<Customer> binder = new Binder<>(Customer.class);

    public SaveWindow(final MyUI myUI, final Customer customer) {
    	super("Save");
    	center();
		setClosable(false);
		setResizable(false);
    	
    	// create
    	if(customer != null) {
    		firstName.setValue(customer.getFirstName());
    		lastName.setValue(customer.getLastName());
    		email.setValue(customer.getEmail());
    		birthdate.setValue(customer.getBirthDate());
    	}
        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        
        VerticalLayout saveContent = new VerticalLayout(firstName, lastName, email, birthdate, buttons);
        setContent(saveContent);
        
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER);

        binder.bindInstanceFields(this);
        binder.setBean(customer);

        save.addClickListener(e -> this.save(myUI, customer));
    }

    private void save(MyUI myUI, Customer customer) {
    	service.save(customer);
    	myUI.updateList();
    	close();
    }
}
