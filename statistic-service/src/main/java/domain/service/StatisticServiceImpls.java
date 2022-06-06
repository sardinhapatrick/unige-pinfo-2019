package domain.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import domain.model.StatisticItem;
import domain.model.StatisticUser;
import domain.model.Categorie;



@ApplicationScoped
@Transactional
@Default
public class StatisticServiceImpls  implements StatisticService {

	@PersistenceContext(unitName="StatisticPU")
	private EntityManager em;

	private static final String LIVRESLAB = "nClicsLivres";
	private static final String MOBILITELAB = "nClicsMobilite";
	private static final String MOBILIERLAB = "nClicsMobilier";
	private static final String ELECTRONIQUELAB = "nClicsElectronique";
	private static final String NOTESLAB = "nClicsNotes";
	private static final String AUTRELAB = "nClicsAutre" ;
	private static final String USRID = "usrid" ;
	private static final String ITEMID = "itemid" ;


	@Override
	public StatisticItem getItemStats(String itemId) {		//retourne les stats générales de l'item sélectionné
		return em.createQuery(	"SELECT s FROM StatisticItem s WHERE s.itemId = :itemid", StatisticItem.class).setParameter(ITEMID, itemId).getSingleResult() ;
	}

	@Override
	public StatisticUser getUserStats(String usrId) {		//ret ourne les stats de l'utilisateur donné pour l'item sélectionné
		return em.createQuery(	"SELECT s FROM StatisticUser s WHERE s.userId = :usrid", StatisticUser.class).setParameter(USRID, usrId).getSingleResult();
	}

	@Override
	public List<StatisticUser> getAllUser() {
		return em.createQuery("SELECT s FROM StatisticUser s", StatisticUser.class).getResultList();
	}

	@Override
	public List<StatisticItem> getAllItem() {
		return em.createQuery("SELECT s FROM StatisticItem s ORDER BY s.category", StatisticItem.class).getResultList();
	}

	@Override
	public void addUserStats(StatisticUser stats) {
		if (em.contains(stats)) {
			throw new IllegalArgumentException("User with id " + stats.getUserId() + " already exists");
		}
		else
			em.persist(stats);
	}

	@Override
	public void addItemStats(StatisticItem stats) {
		if (em.contains(stats)) {
			throw new IllegalArgumentException("Item with id " + stats.getItemId() + " already exists");
		}
		else
			em.persist(stats);
	}

	@Override
	public void removeUserStats(String usrId) {
		em.createQuery(	"DELETE FROM StatisticUser WHERE userId = :usrid").setParameter(USRID, usrId).executeUpdate();
	}

	@Override
	public void removeItemStats(String itemId) {
		em.createQuery(	"DELETE FROM StatisticItem WHERE itemId = :itemid").setParameter(ITEMID, itemId).executeUpdate();
	}

	@Override
	public void incrementUser(String userId, Categorie categorie) {

		Query q = null;
		switch (categorie) {
		case LIVRE:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsLivres = nClicsLivres+1 WHERE userId = :usrid") ;
			break;
		case MOBILITE:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsMobilite = nClicsMobilite+1 WHERE userId = :usrid") ;
			break;
		case MOBILIER:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsMobilier = nClicsMobilier+1 WHERE userId = :usrid") ;
			break;
		case ELECTRONIQUE:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsElectronique = nClicsElectronique+1 WHERE userId = :usrid") ;
			break;
		case COURS:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsNotes = nClicsNotes+1 WHERE userId = :usrid") ;
			break;
		case AUTRE:
			q = em.createQuery(	"UPDATE StatisticUser SET nClicsAutre = nClicsAutre	+1 WHERE userId = :usrid") ;
			break;
		default:
		}
		
		if (q != null)
			q.setParameter(USRID, userId).executeUpdate();
	}

	@Override
	public void incrementItem(String itemId) {
		Query q = em.createQuery(	"UPDATE StatisticItem SET nClicsItem = nClicsItem+1 WHERE itemId = :itemid") ;
		q.setParameter(ITEMID, itemId).executeUpdate();
	}

	@Override
	public SortedMap<Categorie, Long> getUserHighlights(String usrId, int n) {	//retourne les n catégories les + recherchées par cet utilisateur, triées par ordre croissant de nb de recherches
		if (n < 1 || n > 6)
			throw new IllegalArgumentException("invalid number of categories") ;
		return getCategories(usrId, n, false) ;
	}

	@Override
	public SortedMap<Categorie, Long> getCategoryHighlights(int n) {		//retourne les n catégories les + recherchées de façon générale
		if (n < 1 || n > 6)
			throw new IllegalArgumentException("invalid number of categories") ;
		return getCategories("", n, true) ;
	}

	@Override
	public SortedMap<String, Long> getCategoryItemHighlights(Categorie categorie, int n) {		//retourne les n items les + recherchés dans cette catégorie
		if (n < 1)
			throw new IllegalArgumentException("invalid number of items") ;
		return getItems(categorie, n, false) ;
	}

	@Override
	public SortedMap<String, Long> getItemHighlights(int n) {		//retourne les n items les + recherchés de façon générale
		if (n < 1)
			throw new IllegalArgumentException("invalid number of items") ;
		return getItems(null, n, true) ;
	}

	private SortedMap<Categorie, Long> getCategories(String usrId, int n, boolean isGeneral) {
		String[] cols = {MOBILITELAB, MOBILIERLAB, ELECTRONIQUELAB, NOTESLAB, LIVRESLAB, AUTRELAB} ;
		List<Categorie> categories = new ArrayList<> () ;
		long[] nClics = new long[cols.length] ;
		for (int i = 0 ; i < cols.length ; i++) {
			switch(cols[i]) {
			case MOBILITELAB:
				categories.add(Categorie.MOBILITE) ;
				break;
			case MOBILIERLAB:
				categories.add(Categorie.MOBILIER) ;
				break;
			case ELECTRONIQUELAB:
				categories.add(Categorie.ELECTRONIQUE) ;
				break;
			case NOTESLAB:
				categories.add(Categorie.COURS) ;
				break;
			case LIVRESLAB:
				categories.add(Categorie.LIVRE) ;
				break;
			case AUTRELAB:
				categories.add(Categorie.AUTRE) ;
				break;
			default:
			}
			Query q = getQuery(cols[i], isGeneral);
			if (q != null) {
				if (!isGeneral)
					nClics[i] = (long)q.setParameter(USRID, usrId).getSingleResult() ;
				else
					nClics[i] = (long)q.getSingleResult() ;
			}
		}
		
		SortedMap<Categorie, Long> map = new TreeMap<> () ;
		for (int i = 0 ; i < cols.length ; i++)
			map.put(categories.get(i), nClics[i]) ;

		SortedMap<Categorie, Long> highlights = new TreeMap<> ( (Categorie c1, Categorie c2) -> map.get(c1).compareTo(map.get(c2)) > 0 ? -1 : 1 ) ;		//pour trier les catégories par ordre décroissant de nb de recherches

		for (int i = 0 ; i < cols.length ; i++)
			highlights.put(categories.get(i), nClics[i]) ;

		Iterator<Categorie> it = highlights.keySet().iterator() ;
		int k = 0 ;
		while (it.hasNext()) {
			it.next();
			if (k >= n)
				it.remove();
			k++;
		}

		return highlights;
	}
	
	private Query getQuery(String lab, boolean isGeneral) {
		Query q = null ;
		switch(lab) {
		case MOBILITELAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsMobilite FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsMobilite) FROM StatisticUser s", Long.class) ;
			break;
		case MOBILIERLAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsMobilier FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsMobilier) FROM StatisticUser s", Long.class) ;
			break;
		case ELECTRONIQUELAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsElectronique FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsElectronique) FROM StatisticUser s", Long.class) ;
			break;
		case NOTESLAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsNotes FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsNotes) FROM StatisticUser s", Long.class) ;
			break;
		case LIVRESLAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsLivres FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsLivres) FROM StatisticUser s", Long.class) ;
			break;
		case AUTRELAB:
			if (!isGeneral)
				q = em.createQuery(" SELECT nClicsAutre FROM StatisticUser WHERE userId = :usrid", Long.class) ;
			else
				q = em.createQuery(" SELECT SUM(s.nClicsAutre) FROM StatisticUser s", Long.class) ;
			break;
		default:
		}
		return q ;
	}

	private SortedMap<String, Long> getItems(Categorie categorie, int n, boolean isGeneral) {
		List<StatisticItem> items = null ;
		if (isGeneral)
			items = em.createQuery("SELECT a FROM StatisticItem a ORDER BY a.nClicsItem DESC", StatisticItem.class).setMaxResults(n).getResultList();
		else {
			Query q = em.createQuery("SELECT a FROM StatisticItem a WHERE a.category = :categorie ORDER BY a.nClicsItem DESC", StatisticItem.class);
			items = q.setParameter("categorie", categorie).setMaxResults(n).getResultList();
		}

		SortedMap<String, Long> map = new TreeMap<> () ;
		for (int i = 0 ; i < items.size() ; i++)
			map.put(items.get(i).getItemId(), items.get(i).getnClicsItem()) ;

		SortedMap<String, Long> highlights = new TreeMap<> ( (String s1, String s2) -> map.get(s1).compareTo(map.get(s2)) > 0 ? -1 : 1 ) ;		//pour trier les items par ordre décroissant de nb de recherches	

		for (int i = 0 ; i < items.size() ; i++)
			highlights.put(items.get(i).getItemId(), items.get(i).getnClicsItem()) ;

		return highlights;
	}


}
