/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gss;

import gss.Data.EmptyData;
import gss.data.un.HomicideRate;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class Environment {
        
    
    public static enum Need {
        //Maslow Physiological
        Food,
        Water,
        Air,
        Heat,
        
        //Maslow Security
        Health,
        Financial,
        Safety,
        
        //Maslow Social
        Friendship,
        Intimacy,
        Family,
        Community,
        
        //Maslow Esteem
        Recognition,
        
        //Murray Psychogenic
        Abasement,	//To surrender and submit to others, accept blame and punishment. To enjoy pain and misfortune.
        Achievement,	//To accomplish difficult tasks, overcoming obstacles and becoming expert.
        Affiliation,	//To be close and loyal to another person, pleasing them and winning their friendship and attention.
        Aggression,	//To forcefully overcome an opponent, controlling, taking revenge or punishing them.
        Autonomy,	//To break free from constraints, resisting coercion and dominating authority. To be irresponsible and independent.
        Counteraction,	//To make up for failure by trying again, seeking pridefully to overcome obstacles.
        Defendance,	//To defend oneself against attack or blame, hiding any failure of the self.
        Deference,	//To admire a superior person, praising them and yielding to them and following their rules.
        Dominance,	//To control one's environment, controlling other people through command or subtle persuasion.
        Exhibition,	//To impress others through one's actions and words, even if these are shocking.
        HarmAvoidance,	//To escape or avoid pain, injury and death.
        Infavoidance,	//To avoid being humiliated or embarrassed.
        Nurturance,	//To help the helpless, feeding them and keeping them from danger.
        Order,          //To make things clean, neat and tidy.
        Play,           //To have fun, laugh and relax, enjoying oneself.
        Rejection,	//To separate oneself from a negatively viewed object or person, excluding or abandoning it.
        Sentience,	//To seek out and enjoy sensual experiences.
        Sex,            //To form relationships that lead to sexual intercourse.
        Succourance,	//To have one's needs satisfied by someone or something. Includes being loved, nursed, helped, forgiven and consoled.
        Understanding	//To be curious, ask questions and find answers.

    }
    
    public final Set<String> categories = new HashSet();
    public final Map<String, String> categoryIcon = new HashMap();
    protected final List<Data> sources = new LinkedList();
    private final GeoCache geo = new GeoCache();

    public final String dataPath = "/work/survive/cache";
    
    public Environment() {
        super();
        
        geo.load(getDataFile("geocache"));
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    geo.save(getDataFile("geocache"));
                } catch (IOException ex) {
                    Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        }));
        
        addSource(new EmptyData("radnetTotalIsotopes", "Nuclear Isotope Concentration", "Pollution", "atom.png", "Total Isotope Concentration, bQ/M^3"));
        addSource(new EmptyData("nuclearFacilities", "Nuclear Facilities", "Pollution", "nuclear.png", "Number of Reactors"));
        
        addSource(new EmptyData("earthquakesUSGS", "Earthquakes", "Natural Disasters", "quake.png", "Richter Magnitude"));

        addSource(new EmptyData("lifeexpectancyWorldBank", "Life Expectancy", "Health", "people.png", "Years"));

        addSource(new HomicideRate(geo, getDataFile("UN_Homicide.csv") ));
    }
    
    public String getDataFile(String filename) {
        return dataPath + "/" + filename;
    }
    
    public void addSource(Data s) {
        categories.add(s.category);
        sources.add(s);
        if (categoryIcon.get(s.category) == null)
            categoryIcon.put(s.category, s.iconURL);
    }
    
    public List<Data> getSources(String category) {
        List<Data> l = new LinkedList();
        for (Data ds : sources) {
            if (ds.category.equals(category))
                l.add(ds);
        }
        return l;
    }
}
