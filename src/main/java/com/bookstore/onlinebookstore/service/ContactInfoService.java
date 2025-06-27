package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.model.ContactInfo;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactInfoService {

    private static final String CONTACTS_FILE = "src/main/resources/contacts.txt";

    @PostConstruct
    public void initialize() {
        File file = new File(CONTACTS_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ContactInfo> getAllContacts() {
        List<ContactInfo> contacts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONTACTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    contacts.add(new ContactInfo(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        parts[2],
                        parts[3],
                        parts[4]
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private void writeAllContacts(List<ContactInfo> contacts) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONTACTS_FILE, false))) {
            for (ContactInfo contact : contacts) {
                writer.println(String.format("%d,%d,%s,%s,%s",
                    contact.getId(),
                    contact.getUserId(),
                    contact.getNickname(),
                    contact.getAddress(),
                    contact.getPhoneNumber()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ContactInfo> getContactsByUserId(int userId) {
        return getAllContacts().stream()
                .filter(contact -> contact.getUserId() == userId)
                .collect(Collectors.toList());
    }
    
    public ContactInfo getContactById(int contactId) {
        return getAllContacts().stream()
                .filter(contact -> contact.getId() == contactId)
                .findFirst()
                .orElse(null);
    }

    public void addContact(ContactInfo contact) {
        List<ContactInfo> contacts = getAllContacts();
        contact.setId(getNextId());
        contacts.add(contact);
        writeAllContacts(contacts);
    }

    public void updateContact(ContactInfo updatedContact) {
        List<ContactInfo> contacts = getAllContacts();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == updatedContact.getId()) {
                contacts.set(i, updatedContact);
                break;
            }
        }
        writeAllContacts(contacts);
    }

    public void deleteContactById(int contactId) {
        List<ContactInfo> contacts = getAllContacts();
        contacts.removeIf(contact -> contact.getId() == contactId);
        writeAllContacts(contacts);
    }

    private int getNextId() {
        return getAllContacts().stream()
                .mapToInt(ContactInfo::getId)
                .max()
                .orElse(0) + 1;
    }
} 