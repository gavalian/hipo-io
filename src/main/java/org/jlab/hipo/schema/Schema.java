/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.hipo.schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jlab.hipo.data.HipoNodeType;
import org.jlab.hipo.utils.ArrayUtils;

/**
 *
 * @author gavalian
 */
public class Schema {
    
    private Integer group = 1;
    private String  name  = "default";
    
    private Map<Integer,SchemaEntry>    idEntries = new HashMap<Integer,SchemaEntry>();
    private Map<String,SchemaEntry>   nameEntries = new HashMap<String,SchemaEntry>();
    
    public Schema(){
        
    }
    
    public Schema(String format){
        this.setFromText(format);
    }
    
    public Schema(String n, int grp){
        this.setName(n);
        this.setGroup(grp);
    }
    
    public void addEntry(SchemaEntry entry){
        this.idEntries.put(entry.getId(), entry);
        this.nameEntries.put(entry.getName(), entry);
    }
    
    public void addEntry(String n, int id, HipoNodeType type){
        this.addEntry(new SchemaEntry(n,id,type));
    }
    
    public void addEntry(String n, int id, String typeString){
        
        //this.addEntry(new SchemaEntry(n,id,type));
    }
    
    public SchemaEntry  getEntry(String name){
        return this.nameEntries.get(name);
    }
    /**
     * returns entry with given id
     * @param id entry id tag
     * @return schema entry
     */
    public SchemaEntry  getEntry(int id){
        return this.idEntries.get(id);
    }
    
    public final void   setName(String n){name = n;}
    public final void   setGroup(int grp){group = grp;}
    public final String getName(){return name;}
    public final int    getGroup(){return group;}
    /**
     * Checks if this schema is compatible with the schema passed as an argument.
     * for compatibility every entry in current Schema has to exist in the argument 
     * schema and the types have to be the same. 
     * @param schema
     * @return 
     */
    public boolean compatible(Schema schema){
        for(Map.Entry<Integer,SchemaEntry>  entry : this.idEntries.entrySet()){
            int id = entry.getKey();
            SchemaEntry sche = schema.getEntry(id);
            if(sche==null) return false;
            if(sche.getType()!=entry.getValue().getType()) return false;
        }
        return true;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format(" Schema {%12s} , group = {%d}\n", name,group));
        for(Map.Entry<Integer,SchemaEntry> entry : this.idEntries.entrySet()){
            str.append(entry.getValue().toString());
            str.append("\n");
        }
        return str.toString();
    }
    /**
     * Schema entry class for keeping information on each entry
     */
    public static class SchemaEntry {
        
        private String  name = "x";
        private Integer id   = 1;
        private HipoNodeType type = HipoNodeType.UNDEFINED;               
        
        public SchemaEntry(){
            
        }
         
        public SchemaEntry(String n, int i, HipoNodeType t){
          name = n;
          id = i;
          type = t;
        }
        
        public void setName(String n) {name = n;}
        public void setId(Integer i) {id = i;}
        public void setType(HipoNodeType t) {type = t;}
        public int  getId(){return id;}
        public String getName(){return name;}
        public HipoNodeType getType(){return type;}
        
        @Override
        public String toString(){
            return String.format("%4d : %24s %12s", id,name,type.getName());
        }
    }
    /**
     * returns a String representation of the Schema.
     * @return 
     */
    public String getText(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("{%d,%s}",this.getGroup(),this.getName()));
        for(Map.Entry<Integer,SchemaEntry> entry : this.idEntries.entrySet()){
            str.append(String.format("[%d,%s,%s]", entry.getValue().getId(),
                    entry.getValue().getName(),entry.getValue().getType().getName()));
        }
        return str.toString();
    }
    /**
     * Parses text to extract Schema
     * @param text formated schema text produced by getText() method.
     */
    public final void setFromText(String text){
        this.idEntries.clear();
        this.nameEntries.clear();
        String header = ArrayUtils.getBracketStringCurly(text, 0);
        List<String> headerList = ArrayUtils.getArray(header);
        if(headerList.size()!=2){
            return;
        }
        this.group = Integer.parseInt(headerList.get(0));
        this.name  = headerList.get(1);
        
        int counter = 0;
        String entryText = ArrayUtils.getBracketString(text, counter);
        while(entryText!=null){
            List<String> items = ArrayUtils.getArray(entryText);
            if(items.size()==3){
                this.addEntry(items.get(1), Integer.parseInt(items.get(0)), HipoNodeType.getType(items.get(2)));
            }
            counter++;
            entryText = ArrayUtils.getBracketString(text, counter);
        }
    }
    
    public static void main(String[] args){
        Schema schema = new Schema("DC::dgtz",300);
        schema.addEntry("sector", 1, HipoNodeType.INT);
        schema.addEntry("layer",  2, HipoNodeType.BYTE);
        schema.addEntry("ADC",    3, HipoNodeType.INT);
        schema.addEntry("TDC",    4, HipoNodeType.INT);
        System.out.println(schema);
        System.out.println(schema.getText());
        
        Schema newSchema = new Schema();
        newSchema.setFromText(schema.getText());
        System.out.println(newSchema);
    }
}