package com.acrom.crm.seed;

import com.acrom.crm.model.*;
import com.acrom.crm.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customers;
    private final ContactRepository contacts;
    private final DealRepository deals;
    private final ActivityRepository activities;

    public DataSeeder(CustomerRepository customers, ContactRepository contacts,
                      DealRepository deals, ActivityRepository activities) {
        this.customers = customers;
        this.contacts = contacts;
        this.deals = deals;
        this.activities = activities;
    }

    @Override
    public void run(String... args) {
        if (customers.count() > 0) return;

        Customer[] seed = new Customer[]{
                mk("Anadolu Yazılım AŞ", "info@anadoluyazilim.com", "+90 212 555 0101", "Anadolu Yazılım", Customer.Status.ACTIVE,
                        "Önemli ana müşteri, halihazırda ERP modülü kurulu."),
                mk("Ege Teknoloji Ltd", "info@egetek.com", "+90 232 555 0202", "Ege Teknoloji", Customer.Status.LEAD,
                        "Webinar'dan ulaştı."),
                mk("Karadeniz Lojistik", "malik@krdlogistic.com", "+90 312 555 0303", "Karadeniz Kargo", Customer.Status.ACTIVE,
                        "Entegrasyon 3 ay gecikmeli."),
                mk("Marmara Kâğıt", "satis@marmaraka.com", "+90 216 555 0404", "Marmara Kırtasiye", Customer.Status.INACTIVE,
                        "Bütçe onayı bekleniyor."),
                mk("Melis Korkmaz", "melis.k@gmail.com", "+90 532 555 0505", "", Customer.Status.LEAD,
                        "Freelancer, küçük paket."),
                mk("Orion Danışmanlık", "hello@oriondanismanlik.com", "+90 212 555 0606", "Orion Gmbh", Customer.Status.LOST,
                        "Fiyat rekabeti kaybettirdi.")
        };
        List<Customer> savedC = new ArrayList<>();
        for (Customer c : seed) savedC.add(customers.save(c));

        // opens/pairing
        Contact[] cseeds = new Contact[]{
                contact(savedC.get(0), "Deniz Aksoy", "CTO", "deniz@anadoluyazilim.com"),
                contact(savedC.get(0), "Aylin Yılmaz", "Satış Sorumlusu", "aylin@anadoluyazilim.com"),
                contact(savedC.get(1), "Kemal Doğan", "CEO", "kemal@egetek.com"),
                contact(savedC.get(2), "Rehan Malik", "Ops Müdürü", "rehan.malik@krdlogistic.com"),
                contact(savedC.get(4), "Melis Korkmaz", "Kurucu", "melis.k@gmail.com")
        };
        for (Contact c : cseeds) contacts.save(c);

        deal(savedC.get(0), "Kurumsal ERP modülü", new BigDecimal("120000"), Deal.Stage.WON);
        deal(savedC.get(0), "Bakım paketi 2026", new BigDecimal("24000"), Deal.Stage.PROPOSAL);
        deal(savedC.get(1), "Web sitesi yenileme", new BigDecimal("45000"), Deal.Stage.QUALIFIED);
        deal(savedC.get(2), "Entegrasyon hizmeti", new BigDecimal("88000"), Deal.Stage.PROPOSAL);
        deal(savedC.get(3), "Dijital dönüşüm", new BigDecimal("60000"), Deal.Stage.LOST);
        deal(savedC.get(4), "Kişisel web sitesi", new BigDecimal("5000"), Deal.Stage.NEW);
        deal(savedC.get(5), "Danışmanlık paketi", new BigDecimal("150000"), Deal.Stage.LOST);
        deal(savedC.get(1), "SEO paketi", new BigDecimal("12000"), Deal.Stage.NEW);

        activity(savedC.get(0), Activity.Type.MEETING, "Ürün demo sunumu yapıldı, olumlu sinyaller var.", -6);
        activity(savedC.get(0), Activity.Type.CALL, "Bütçe onayı sonrası teklif gönderilecek.", -3);
        activity(savedC.get(1), Activity.Type.EMAIL, "Webinar kaydı sonrası tanıma maili gönderildi.", -9);
        activity(savedC.get(2), Activity.Type.NOTE, "3 ay gecikmeli entegrasyon için müzakere notu.", -2);
        activity(savedC.get(4), Activity.Type.CALL, "İlk görüşme: peket küçük, hızlı konuşmalı.", -1);
    }

    private Customer mk(String name, String email, String phone, String company, Customer.Status st, String notes) {
        Customer c = new Customer();
        c.name = name;
        c.email = email;
        c.phone = phone;
        c.company = company;
        c.status = st;
        c.notes = notes;
        c.createdAt = LocalDateTime.now().minusDays(30);
        return c;
    }

    private Contact contact(Customer c, String name, String title, String email) {
        Contact t = new Contact();
        t.customer = c;
        t.name = name;
        t.title = title;
        t.email = email;
        return t;
    }

    private void deal(Customer c, String title, BigDecimal amount, Deal.Stage stage) {
        Deal d = new Deal();
        d.customer = c;
        d.title = title;
        d.amount = amount;
        d.stage = stage;
        deals.save(d);
    }

    private void activity(Customer c, Activity.Type type, String body, int daysAgo) {
        Activity a = new Activity();
        a.customer = c;
        a.type = type;
        a.body = body;
        a.createdAt = LocalDateTime.now().plusDays(daysAgo);
        activities.save(a);
    }
}
