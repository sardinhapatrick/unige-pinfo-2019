package domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import domain.model.Item;
import eu.drus.jpa.unit.api.JpaUnit;

@Transactional
@ExtendWith(JpaUnit.class)
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
	
	@Spy
	@PersistenceContext(unitName = "ItemsPUTest")
	EntityManager em;
	
	@InjectMocks
	private ItemServiceImpl itemserviceimpl;
	
	private int initDataStore() {
		em.clear();
		List<Item> items;
		Item item1 = new Item("1234","étagère",200,"mobilier","un mobilier","neuf", "");
		Item item2 = new Item("1234","chaise",300,"mobilier","un autre mobilier","neuf","");
		Item item3 = new Item("123","vtt",250,"velo","un vehicule","neuf","");
		Item item4 = new Item("123","sofa",400,"mobilier","un mobilier encore","neuf","");
		Item item5 = new Item("1235","velo",600,"velo","sapristi un vehicule","neuf","");
		Item item6 = new Item("1235","magazine",100,"livre","mon bouquin","neuf","");
		itemserviceimpl.addItem(item1);
		itemserviceimpl.addItem(item2);
		itemserviceimpl.addItem(item3);
		itemserviceimpl.addItem(item4);
		itemserviceimpl.addItem(item5);
		itemserviceimpl.addItem(item6);
		items = itemserviceimpl.getAll();
		int size = items.size();
		return size;
	}
	
	@Test
	void createItemTest(){
		int size = initDataStore();
		try {
			Item item = new Item("1236","Le seigneur des anneaux",200,"livre","Un vrai bouquin","neuf","");
			itemserviceimpl.create(item);
			assertEquals(size+1, itemserviceimpl.getAll().size());
			itemserviceimpl.create(item);
		}
		catch (IllegalArgumentException exc) {
			assertEquals("Ad already exists", exc.getMessage());
		}
	}
	
	
	@Test
	void getHighlightTest(){
		int size = initDataStore();
		assertEquals(size, itemserviceimpl.getHighlight("tom").size());
		
	}
	
	@Test
	void getAllTest(){
		int size = initDataStore();
		assertEquals(size, itemserviceimpl.getAll().size());
		
	}
	
	
	@Test
	void getBySearchTest() {
		initDataStore3();
		Item item1 = new Item("1","Velofm electrique",200, "velo", "papapa","neuf");item1.setId("1");
		Item item2 = new Item("1","gpomtvelosnf",50,"velo", "pfonf", "neuf");item2.setId("2");
		Item item3 = new Item("1","vtt",400, "velo", "fvelgnfo", "neuf");item3.setId("3");
		Item item4 = new Item("1","sofa",600, "velo", "lrlmvelo moteur", "neuf");item4.setId("4");
		Item item5 = new Item("1","velo fovno electrique",200, "velo", "papapa","neuf");item5.setId("5");
		Item item6 = new Item("1","gpomtvelosnf",50,"mobiler", "pfonf", "neuf");item6.setId("6");
		List <Item> testvelo = new ArrayList<Item>();List <Item> testcat = new ArrayList<Item>(); List<Item> testpri =  new ArrayList<Item>();
		testvelo.add(item1);testvelo.add(item2);testvelo.add(item4);testvelo.add(item5);testvelo.add(item6);
		testcat.add(item1);testcat.add(item2);testcat.add(item4);testcat.add(item5);
		testpri.add(item1);testpri.add(item3);testpri.add(item5);
		assertEquals(testvelo.toString(), itemserviceimpl.getBySearch("velo", "all", "all", 1, 10000, 1).toString());
		assertEquals(testvelo.toString(), itemserviceimpl.getBySearch("velo", "all", "neuf", 1, 10000, 1).toString());
		assertEquals(testcat.toString(), itemserviceimpl.getBySearch("velo", "velo", "neuf", 1, 10000, 1).toString());
		assertEquals(testpri.toString(), itemserviceimpl.getBySearch(null, "velo", "all", 200, 400, 1).toString());
	}
		
	@Test
	void ToStringTest() {
		Item item1 = new Item("1","velofm electrique",200, "velo", "papapa","neuf","");
		item1.setId("1");
		String s = "Item [id = 1 usrid=1 name=velofm electrique, price=200.0, category=velo, description=papapa, state=neuf]";
		assertEquals(s, item1.toString());
	}
	
	
	private void initDataStore3() {
		em.clear();
		int deletedCount = em.createQuery("DELETE FROM Item").executeUpdate();
		Item item1 = new Item("1","Velofm electrique",200, "velo", "papapa","neuf","");item1.setId("1");
		Item item2 = new Item("1","gpomtvelosnf",50,"velo", "pfonf", "neuf","");item2.setId("2");
		Item item3 = new Item("1","vtt",400, "velo", "fvelgnfo", "neuf","");item3.setId("3");
		Item item4 = new Item("1","sofa",600, "velo", "lrlmvelo moteur", "neuf","");item4.setId("4");
		Item item5 = new Item("1","velo fovno electrique",200, "velo", "papapa","neuf","");item5.setId("5");
		Item item6 = new Item("1","gpomtvelosnf",50,"mobiler", "pfonf", "neuf","");item6.setId("6");
		itemserviceimpl.addItem(item1);
		itemserviceimpl.addItem(item2);
		itemserviceimpl.addItem(item3);
		itemserviceimpl.addItem(item4);
		itemserviceimpl.addItem(item5);
		itemserviceimpl.addItem(item6);
	}
	
	private List<Item> getItems() {
		List<Item> it = new ArrayList<>();
		long numberOfCpty = Math.round((Math.random() * 10));
		for (int i = 0; i < numberOfCpty; i++) {
			it.add(createItem());
		}
		return it;
	}


	private Item createItem(){
		Item i = new Item();
		i.setName(UUID.randomUUID().toString());
		i.setCategory(UUID.randomUUID().toString());
		i.setDescription(UUID.randomUUID().toString());
		i.setState((1 + (int)(Math.random() * ((5 - 1) + 1))) + "");
		i.setPrice(0 + (int)(Math.random() * ((10000 - 0) + 1)));
		return i;
	}


	private int initDataStore2() {
		em.clear();
		List<Item> items;
		Item item1 = new Item("1234","étagère",200,"mobilier","un mobilier","neuf");
		Item item2 = new Item("1236","chaise",300,"mobilier","un autre mobilier","neuf");
		Item item3 = new Item("1235","vtt",250,"velo","un vehicule","neuf");
		Item item4 = new Item("1237","sofa",400,"mobilier","un mobilier encore","neuf");
		Item item5 = new Item("1239","velo",600,"velo","sapristi un vehicule","neuf");
		Item item6 = new Item("1238","magazine",100,"livre","mon bouquin","neuf");
		itemserviceimpl.addItem(item1);
		itemserviceimpl.addItem(item2);
		itemserviceimpl.addItem(item3);
		itemserviceimpl.addItem(item4);
		itemserviceimpl.addItem(item5);
		itemserviceimpl.addItem(item6);
		items = itemserviceimpl.getItem("1234");
		int size = items.size();
		return size;
	}
	
	@Test
	void getItemTest() {
		int size = initDataStore2();
		assertEquals(size, itemserviceimpl.getItem("1234").size());
	}
	
	@Test
	void getItemidTest() {
		String id = UUID.randomUUID().toString();
		Item item = new Item(id, "18248", "uifei", 4, "livre", "doenfr", "neuf","");
		em.persist(item);
		assertEquals(item, itemserviceimpl.getItemid(id).get(0));
	}
	
	private int initDataStore4() {
		em.clear();
		List<Item> items;
		Item item1 = new Item("1234","étagère",200,"mobilier","un mobilier","neuf");
		Item item2 = new Item("1234","chaise",300,"mobilier","un autre mobilier","neuf");
		Item item3 = new Item("123","vtt",250,"velo","un vehicule","neuf");
		Item item4 = new Item("123","sofa",400,"mobilier","un mobilier encore","neuf");
		Item item5 = new Item("1235","velo",600,"velo","sapristi un vehicule","neuf");
		Item item6 = new Item("1235","magazine",100,"livre","mon bouquin","neuf");
		itemserviceimpl.addItem(item1);
		itemserviceimpl.addItem(item2);
		itemserviceimpl.addItem(item3);
		itemserviceimpl.addItem(item4);
		itemserviceimpl.addItem(item5);
		itemserviceimpl.addItem(item6);
		items = itemserviceimpl.getAll();
		int size = items.size();
		return size;
	}
	
	@Test 
	void removeItemTest() {
		int size = initDataStore4();
		Item item = new Item("1236","Le seigneur des anneaux",30,"livre","un vrai bouquin","neuf");
		Item item2 = new Item("1236","Le pianiste",50,"livre","un bouquin bof","neuf");
		em.persist(item);
		em.persist(item2);
		itemserviceimpl.removeItem(item2);
		assertEquals(size+2, itemserviceimpl.getAll().size());
	}
	
	@Test 
	void updateItemTest() {
		Item i = new Item("18248", "uifei", 4, "livre", "doenfr", "neuf","");
		em.persist(i);
		Item newi = new Item(i.getId(),"18248", "ciao", 4, "livre", "doenfr", "neuf","");
		itemserviceimpl.updateItem(newi);
		i.setName("ciao");
		assertEquals(newi, itemserviceimpl.getBySearch("ciao", "livre", "neuf", 3, 5, 1).get(0));
	}	
	
	@Test
	void modelTest() {
		Item item = new Item("1236","Le seigneur des anneaux",30,"livre","un vrai bouquin","neuf","");
		item.setUsrId("1234");
		String newId = UUID.randomUUID().toString();
		item.setId(newId);
		item.setDescription("un livre");
		item.setCategory("mobilier");
		item.setState("usagé");
		item.setPrice(40);
		item.setSold(false);
		item.setImages("imagetest");
		assertEquals("1234",item.getUsrId());
		assertEquals(item.getId(),newId);
		assertEquals("un livre", item.getDescription());
		assertEquals("mobilier", item.getCategory()) ;
		assertEquals("usagé", item.getState()) ;
		assertEquals(40, item.getPrice()) ;
		assertEquals(false, item.getSold()) ;
		assertEquals("imagetest", item.getImages()) ;
		assertEquals(item.toString(),"Item [id = "+newId+ " usrid="+ "1234" +" name=" + "Le seigneur des anneaux" + ", price=" + 40.0 + ", category=" + "mobilier" + ", description=" + "un livre"
				+ ", state=usagé" + "]");
	}
	
}

