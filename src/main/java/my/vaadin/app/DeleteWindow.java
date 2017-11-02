package my.vaadin.app;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class DeleteWindow extends Window {
	
	private Button confirmDelete;
	private Button cancelDelete;
	
	private CustomerService service = CustomerService.getInstance();
	
	public DeleteWindow(final MyUI myUI, final Customer customer) {
		super("Delete");
		center();
		setClosable(false);
		setResizable(false);
		confirmDelete = new Button("Supprimer");
		confirmDelete.addClickListener(e -> {
			service.delete(customer);
			myUI.updateList();
			close();
        });
		cancelDelete = new Button("Annuler", event -> close());
        HorizontalLayout deleteButtons = new HorizontalLayout(confirmDelete, cancelDelete);
        VerticalLayout deleteContent = new VerticalLayout(new Label("Voulez-vous vraiment supprimer l'utilisateur sélectionné ?"), deleteButtons);
        deleteContent.setComponentAlignment(deleteButtons, Alignment.BOTTOM_CENTER);
        setContent(deleteContent);
	}

}
