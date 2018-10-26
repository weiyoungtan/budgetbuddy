package model;

import model.categories.*;
import model.exceptions.CloseToOverspendingException;
import model.exceptions.ExceededTotalException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BudgetBuddy implements Loadable, Saveable {

    private double total;
    private double limit;
    private ArrayList<Entry> entries;

    public BudgetBuddy() {
        total = 0;
        limit = 0;
        entries = new ArrayList();
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public double getLimit() {
        return this.limit;
    }

    public double getTotal() {
        return this.total;
    }

    public void createNewEntry(int choice, String name, double entryAmount) {
        Category category;
        switch (choice) {
            case 1: category = new Food(); break;
            case 2: category = new Groceries(); break;
            case 3: category = new Entertainment(); break;
            case 4: category = new Bills(); break;
            case 5: category = new Utilities(); break;
            case 6: category = new Rent(); break;
            default: category = new Miscellaneous(); break;
        }
        Entry entry = new Entry(category, name, entryAmount);
        entries.add(entry);
        try {
            checkBudget(entry);
        } catch (ExceededTotalException e) {
            System.out.printf("LIMIT EXCEEDED BY $%.2f!!! \nPlease be aware of how much you spent!!\n\n", getTotal() - getLimit());
        } catch (CloseToOverspendingException e) {
            System.out.printf("You have spent $%.2f so far. \nThis is more than 90%% of your budget. \nPlease beware of your spendings.\n\n", getTotal());
        }
    }

    public void checkBudget(Entry entry) throws ExceededTotalException, CloseToOverspendingException {
        total += entry.entryAmount;
        if (getTotal() > getLimit())
            throw new ExceededTotalException();
        else if (getTotal()/getLimit() > 0.9)
            throw new CloseToOverspendingException();
        else
            System.out.printf("You have spent $%.2f so far. \n", getTotal());
    }

    public void viewSummary() {
        for (Entry e: entries) {
            System.out.printf("Category: %s \n", e.getCategory());
            System.out.printf("Tag: %s \n", e.getEntryName());
            System.out.printf("Amount: $%.2f \n\n", e.getEntryAmount());
        }
    }

    @Override
    public void save() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("BudgetBuddySave.txt","UTF-8");
        writer.println(this.total);
        writer.println(this.limit);
        for (Entry e: entries) {
            writer.printf("%s;%s;$%.2f\n", e.getCategory(), e.getEntryName(), e.getEntryAmount());
        }
        writer.close();
    }

    @Override
    public void load() throws IOException {
        List<String> lines  = Files.readAllLines(Paths.get("BudgetBuddySave.txt"));
        if (isSaveFileEmpty(lines)){
            throw new IOException();
        }
        this.total = Double.parseDouble(lines.get(0));
        this.limit = Double.parseDouble(lines.get(1));
        for (int i = 2; i < lines.size(); i++) {
            lines.get(i).split(";");
        }
        System.out.println("Memory loaded.");
    }

    public boolean isSaveFileEmpty(List<String> lines) {
        boolean empty = false;
        for (String s: lines ){
            if (s.isEmpty())
                empty = true;
        }
        return empty;
    }



}
