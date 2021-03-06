package net.digihippo.ltt.soap;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.1.9.1
//
// Created by Quasar Development at 02-12-2015
//
//---------------------------------------------------





public class WWHEnums
{

    public enum FilterType
    {
        
        to(0),
        
        from(1);
        
        private int code;
        
        FilterType(int code ){
            this.code = code;
        }
    
        public int getCode(){
		    return code;
	    }
    

        public static FilterType fromString(String str)
        {
            if (str.equals("to"))
                return to;
            if (str.equals("from"))
                return from;
		    return null;
        }
    }

    public enum ServiceType
    {
        
        train(0),
        
        bus(1),
        
        ferry(2);
        
        private int code;
        
        ServiceType(int code ){
            this.code = code;
        }
    
        public int getCode(){
		    return code;
	    }
    

        public static ServiceType fromString(String str)
        {
            if (str.equals("train"))
                return train;
            if (str.equals("bus"))
                return bus;
            if (str.equals("ferry"))
                return ferry;
		    return null;
        }
    }

}