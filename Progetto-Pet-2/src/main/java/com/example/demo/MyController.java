
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;

import jakarta.mail.MessagingException;

import java.util.ArrayList;

@Controller
public class MyController {
	
	@Autowired
	JdbcTemp p1;
	
	@Autowired
	EmailService emailService;
	
	@Value("${stripe.currency}")
	private String currency;
	
	@Value("${stripe.secretKey}")
    private String stripeSecretKey;
	
	@GetMapping("/home")
	public String homePage() {
		return "home";
	}
	
	@GetMapping("/private/form")
	public String getForm(Model model) {
		
		ArrayList <pet> lista = p1.getLista();
		
		model.addAttribute("lista", lista);
		
		return "form";
	}
	@ResponseBody
	@PostMapping("/submit")
	public String gestisciForm(@RequestParam("nome") String nome, 
			@RequestParam("marca") String marca,
			@RequestParam("prezzo") double prezzo,
			@RequestParam("url") String url
			) {
		
		p1.insertGame(nome, marca, prezzo, url);
				return nome + " aggiunto con successo";
		
		
		
		
	}
	
	@ResponseBody
	@PostMapping("/update")
	public String updatePrezzo(@RequestParam("nome") String nome,
			@RequestParam("prezzo") double prezzo
			) {
		p1.setPrezzo(nome, prezzo);
		return nome + " aggiornato con successo";
		
	}
	@ResponseBody
	@PostMapping("/delete")
	public String delete(@RequestParam("nome") String nome) {
		
		p1.delete(nome);
		
		
		return nome + "Cancellato con successo";
	}
	
	@GetMapping("/")
	public String getStore(Model model) {
ArrayList <pet> lista = p1.getLista();
		
		model.addAttribute("lista", lista);
		return "store";
	}
	
	@PostMapping("/buy")
	public String recap(@RequestParam("selected") ArrayList<Integer> selected,
			@RequestParam("mail") String mail, @RequestParam("token") String token, 
			
			Model model) throws MessagingException {
		
		int somma = 0;
		ArrayList <pet> lista = p1.getLista();
		ArrayList <petA> listaP = new ArrayList<>();
		ArrayList <String> listaU = new ArrayList <>();
				
	    String soggetto = "Hai acquistato: ";
				
				for (int i = 0; i < lista.size(); i++) {
			
			
			
			if (selected.get(i) > 0 ) {
			//System.out.println("Hai acquistato " + listaP.get(i).getNome() + " marca " + listaP.get(i).getMarca()  );
			//System.out.println("In " + selected.get(i) + " pezzi");
			somma += selected.get(i) * lista.get(i).getPrezzo();
			petA g1 = new petA(lista.get(i).getNome(), lista.get(i).getMarca(),(lista.get(i).getPrezzo()*selected.get(i)),
					lista.get(i).getUrl(), selected.get(i) );
			listaP.add(g1);
			p1.change(lista.get(i).getNome(), selected.get(i));
			listaU.add(lista.get(i).getUrl());
			soggetto +=   lista.get(i).getNome() + ", ";
			
		}
		}
	    soggetto += " La somma totale da pagare è: " + somma + " euro";
		emailService.sendEmailWithImage(mail, "mail da talentform-giocattoli", soggetto, listaU);
		
		//System.out.println("La somma totale da pagare è: " + somma + " euro");
		model.addAttribute("lista", listaP);
		model.addAttribute("somma", somma);
		
		  try {
	            // Imposta la chiave segreta di Stripe
	            Stripe.apiKey = stripeSecretKey;

	            // Crea una richiesta di pagamento
	            ChargeCreateParams params = ChargeCreateParams.builder()
	                    .setAmount((long)  somma * 100) // Importo in centesimi
	                    .setCurrency("eur")
	                    .setSource(token) // Usa il token di test fornito
	                    .build();
	            
	            
	            Charge charge = Charge.create(params);
	            
	            System.out.println("Pagamento completato: " + charge.toJson());
	            
	            System.out.println("Visualizza ricevuta: " +  charge.getReceiptUrl());
	            
	         //risult = "Pagamento andato a buon fine";
	         
	         model.addAttribute("urlRicevuta",charge.getReceiptUrl());
         //  ok = true;
	            
	            
	        } catch (StripeException e) {
	        	
	        	//risult = "Pagamento non riuscito, riprova";
	        	//ok = false;
	        	 
	        }	
	    		return "recap";
	        	
	        }	
	        	
	        	
	        
		    @CrossOrigin(origins = "*")
			@ResponseBody
			@GetMapping("/lista")
			public ArrayList<pet> getLista(){
				
				ArrayList<pet> lista = p1.getLista();
				
				return lista;
	
	}

}
