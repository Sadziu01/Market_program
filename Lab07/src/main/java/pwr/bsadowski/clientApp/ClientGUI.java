package pwr.bsadowski.clientApp;

import pwr.bsadowski.exceptions.EmptyCartException;
import pwr.bsadowski.exceptions.WrongNumberException;

import model.OrderLine;
import model.Order;
import model.Status;
import model.ItemType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;


public class ClientGUI extends JFrame {

	private JPanel contentPane, switchPane;
	private CardLayout cl = new CardLayout();
	private DefaultTableModel tableModelShop, tableModelCart, tableModelOrders;
	static String name;
	private JTextField textFieldItem, textFieldLabel, textFieldQuantity, textIdToChangeStatus;
	private JButton btnReload, btnSubscribe, btnUnsubscribe;
	private JLabel lblReload;
	private ClientApp cLientController;
	DecimalFormat df = new DecimalFormat("#.##");

	/**
	 * Create the frame.
	 */
	public ClientGUI() throws RemoteException {

		name = JOptionPane.showInputDialog(null, "Enter name:", "Client", JOptionPane.QUESTION_MESSAGE);

		cLientController = new ClientApp();
		cLientController.registerClient();
		cLientController.weTomListe();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 630, 385);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panelMenu = new JPanel();
		panelMenu.setBounds(10, 10, 594, 39);
		contentPane.add(panelMenu);
		panelMenu.setLayout(null);
		
		JButton btnShopSite = new JButton("Shop");
		btnShopSite.addActionListener(e -> {
			cl.show(switchPane, "1");
			cLientController.getFrame().updateOrdersTable();
		});

		btnShopSite.setBounds(10, 10, 85, 21);
		panelMenu.add(btnShopSite);
		
		JButton btnCart = new JButton("Cart");
		btnCart.addActionListener(e ->{
			cl.show(switchPane, "2");
			cLientController.getFrame().updateCartTable();
		});

		btnCart.setBounds(499, 10, 85, 21);
		panelMenu.add(btnCart);
		
		JButton btnMyOrders = new JButton("My Orders");
		btnMyOrders.addActionListener(e -> {
			cl.show(switchPane, "3");
			cLientController.getFrame().updateShopTable();
		});

		btnMyOrders.setBounds(388, 10, 101, 21);
		panelMenu.add(btnMyOrders);

		switchPane = new JPanel();
		switchPane.setBounds(10, 59, 594, 279);
		contentPane.add(switchPane);
		switchPane.setLayout(cl);
		
		JPanel panelShop = new JPanel();
		panelShop.setBackground(new Color(128, 128, 192));
		panelShop.setBounds(0, 0, 582, 279);
		switchPane.add("1", panelShop);

		String[] columnsShop = {"Id", "Name", "Price", "Category"};
		tableModelShop = new DefaultTableModel(columnsShop, 0);
		panelShop.setLayout(null);
		JTable tableShop = new JTable(tableModelShop);
		JScrollPane planeShop = new JScrollPane(tableShop);
		planeShop.setBounds(0, 0, 594, 136);
		panelShop.add(planeShop);
		
		JPanel formShop = new JPanel();
		formShop.setBounds(0, 134, 594, 145);
		panelShop.add(formShop);
		formShop.setLayout(null);
		
		textFieldItem = new JTextField();
		textFieldItem.setBounds(284, 29, 96, 19);
		formShop.add(textFieldItem);
		textFieldItem.setColumns(10);
		
		textFieldLabel = new JTextField();
		textFieldLabel.setBounds(284, 59, 96, 19);
		formShop.add(textFieldLabel);
		textFieldLabel.setColumns(10);
		
		textFieldQuantity = new JTextField();
		textFieldQuantity.setBounds(284, 89, 96, 19);
		formShop.add(textFieldQuantity);
		textFieldQuantity.setColumns(10);
		
		JButton btnAddToCart = new JButton("Add To Cart");
		btnAddToCart.addActionListener(e -> {
			if(Integer.parseInt(textFieldQuantity.getText()) < 1){
				throw new WrongNumberException("Number cannot be less than 1.");
			}
			else{
				OrderLine ol = new OrderLine(cLientController.getClientLookupList().get(Integer.parseInt(textFieldItem.getText())), Integer.parseInt(textFieldQuantity.getText()),
						textFieldLabel.getText());
				cLientController.getClientShoppingCartList().add(ol);

				textFieldItem.setText("");
				textFieldLabel.setText("");
				textFieldQuantity.setText("");
			}
		});
		btnAddToCart.setBounds(242, 119, 101, 21);
		formShop.add(btnAddToCart);

		JLabel lblChooseItem = new JLabel("Item id");
		lblChooseItem.setBounds(213, 33, 45, 13);
		formShop.add(lblChooseItem);
		
		JLabel lblLabel = new JLabel("Label");
		lblLabel.setBounds(213, 63, 45, 13);
		formShop.add(lblLabel);
		
		JLabel lblQuantity = new JLabel("Quantity");
		lblQuantity.setBounds(213, 93, 58, 13);
		formShop.add(lblQuantity);
		
		JPanel panelCart = new JPanel();
		panelCart.setBackground(new Color(0, 128, 192));
		panelCart.setBounds(0, 0, 582, 279);
		switchPane.add("2",panelCart);
		panelCart.setLayout(null);

		String[] columnsCart = {"Name", "Quantity", "Price"};
		tableModelCart = new DefaultTableModel(columnsCart, 0);
		panelCart.setLayout(null);
		JTable tableCart = new JTable(tableModelCart);
		JScrollPane planeCart = new JScrollPane(tableCart);
		planeCart.setBounds(0, 0, 594, 197);
		panelCart.add(planeCart);

		JPanel formCart = new JPanel();
		formCart.setBounds(0, 196, 594, 83);
		panelCart.add(formCart);
		formCart.setLayout(null);
		
		JButton btnPlaceOrder = new JButton("Place Order");
		btnPlaceOrder.addActionListener(e -> {
			if(cLientController.getClientShoppingCartList().isEmpty()){
				throw new EmptyCartException("Cart is empty.");
			}

			PlaceOrder newPlaceOrder = new PlaceOrder();

			Order order = new Order(cLientController.getId());
			for(var orderLine : cLientController.getClientShoppingCartList()){
				order.addOrderLine(orderLine);
			}

			try {
				newPlaceOrder.setId(cLientController.getShop().placeOrder(order));
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}
			newPlaceOrder.setOrder(order);
			newPlaceOrder.setStatus(Status.NEW);

			JOptionPane.showMessageDialog(null, "You paid: " + df.format(order.getCost()));

			cLientController.getClientSOList().add(newPlaceOrder);
			cLientController.getFrame().updateOrdersTable();
			cLientController.getClientShoppingCartList().clear();
			cLientController.getFrame().updateCartTable();
		});

		btnPlaceOrder.setBounds(236, 35, 112, 21);
		formCart.add(btnPlaceOrder);
		
		JButton btnClearCart = new JButton("Clear");
		btnClearCart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cLientController.getClientShoppingCartList().clear();
				cLientController.getFrame().updateCartTable();
			}
		});
		btnClearCart.setBounds(508, 11, 76, 23);
		formCart.add(btnClearCart);
		
		JPanel panelOrders = new JPanel();
		panelOrders.setBackground(new Color(128, 128, 255));
		panelOrders.setBounds(0, 0, 582, 279);
		switchPane.add("3", panelOrders);
		panelOrders.setLayout(null);

		String[] columnsOders = {"Order ID", "Name", "Advert", "Quantity", "Price", "Status"};
		tableModelOrders = new DefaultTableModel(columnsOders, 0);
		panelOrders.setLayout(null);
		JTable tableOrders = new JTable(tableModelOrders);
		JScrollPane planeOrders = new JScrollPane(tableOrders);
		planeOrders.setBounds(0, 0, 594, 197);
		panelOrders.add(planeOrders);

		JPanel formOrders = new JPanel();
		formOrders.setBounds(0, 196, 594, 83);
		panelOrders.add(formOrders);
		formOrders.setLayout(null);
		
		btnSubscribe = new JButton("Subscribe");
		btnSubscribe.addActionListener(e -> {

			try {
				cLientController.getShop().subscribe(cLientController.getStatusListener(), cLientController.getId());
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}


			btnSubscribe.setVisible(false);
			btnUnsubscribe.setVisible(true);
			btnReload.setVisible(false);
			lblReload.setVisible(false);
			textIdToChangeStatus.setVisible(false);
		});
		btnSubscribe.setBounds(327, 36, 108, 21);
		formOrders.add(btnSubscribe);

		textIdToChangeStatus = new JTextField();
		textIdToChangeStatus.setBounds(217, 20, 86, 20);
		formOrders.add(textIdToChangeStatus);
		textIdToChangeStatus.setColumns(10);

		lblReload = new JLabel("Id to Reload");
		lblReload.setBounds(129, 23, 78, 14);
		formOrders.add(lblReload);
		
		btnReload = new JButton("Reload");
		btnReload.addActionListener(e -> {
			int id = Integer.parseInt(textIdToChangeStatus.getText());

			try {
				cLientController.updateStatus(id, cLientController.getShop().getStatus(id));
			}catch (IndexOutOfBoundsException exception){
				System.out.println("Illegal argument");
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}
		});

		btnReload.setBounds(218, 51, 85, 21);
		formOrders.add(btnReload);
		btnReload.setVisible(true);
		
		btnUnsubscribe = new JButton("UnSubscribe");
		btnUnsubscribe.addActionListener(e -> {

			try {
				cLientController.getShop().unsubscribe(cLientController.getId());
			} catch (RemoteException ex) {
				throw new RuntimeException(ex);
			}

			btnSubscribe.setVisible(true);
			btnUnsubscribe.setVisible(false);
			btnReload.setVisible(true);
			lblReload.setVisible(true);
			textIdToChangeStatus.setVisible(true);
		});
		btnUnsubscribe.setBounds(327, 36, 108, 21);
		formOrders.add(btnUnsubscribe);
		btnUnsubscribe.setVisible(false);

		cl.show(switchPane, "1");
	}

	public void updateShopTable(){
		tableModelShop.setRowCount(0);
		for (ItemType it : cLientController.getClientLookupList()) {
			tableModelShop.insertRow(tableModelShop.getRowCount(), new Object[]{tableModelShop.getRowCount(), it.getName(), it.getPrice(), it.getCategory()});
		}
	}

	public void updateCartTable(){
		tableModelCart.setRowCount(0);
		for(OrderLine ol : cLientController.getClientShoppingCartList()){
			tableModelCart.insertRow(tableModelCart.getRowCount(), new Object[]{ol.getIt().getName(), ol.getQuantity(), ol.getCost()});
		}
	}

	public void updateOrdersTable(){
		tableModelOrders.setRowCount(0);
		for(PlaceOrder so : cLientController.getClientSOList()){
			for (int i = 0; i < so.getOrder().getOll().size(); i++) {
				tableModelOrders.addRow(new Object[]{so.getId() ,so.getOrder().getOll().get(i).getIt().getName(),
						so.getOrder().getOll().get(i).getAdvert(), so.getOrder().getOll().get(i).getQuantity(),
						so.getOrder().getOll().get(i).getCost(), so.getStatus()});
			}
		}
	}
}
