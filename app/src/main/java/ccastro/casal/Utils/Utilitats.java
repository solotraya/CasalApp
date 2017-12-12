package ccastro.casal.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Carlos on 23/11/2017.
 */

public class Utilitats {
    public static String getFechaFormatSpain(String data){
        String fechaMostrar [] =  data.split(" ");
        return fechaMostrar[2]+"/"+fechaMostrar[1]+"/"+fechaMostrar[0];
    }
    public static String obtenerFechaActual (){
        Calendar ahoraCal = Calendar.getInstance();
        // PARECE QUE EL MES EMPIEZA DESDE 0, HAY QUE SUMAR UNO.
        ahoraCal.getTime();
        String fecha;

        fecha = ahoraCal.get(Calendar.YEAR)+" "+(ahoraCal.get(Calendar.MONTH)+1)+
                " "+ahoraCal.get(Calendar.DATE);

        return fecha;
    }
    public static Boolean diaHabil(String fechaString, Integer domingo){
        boolean diaHabil = false;

        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;
        try {
            fecha = formatoDelTexto.parse(fechaString);
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(fecha);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == domingo ) {
                diaHabil= false;
            } else diaHabil = true;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return diaHabil;
    }
    public static String obtenerHoraActual (){
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("HH:mm");   // HH formato 24 horas. hh formato 12 horas.
        return formateador.format(ahora);
    }
}
