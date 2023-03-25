/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lfd;
import java.io.*;
/**
 * Discretization is the process of converting numerical values into categorical values. We propose a new data-driven discretization technique called low frequency discretizer (LFD) that does not require any user input. LFD uses low frequency values as cut points and thus reduces the information loss due to discretization. It uses all other categorical attributes and any numerical attribute that has already been categorized. It considers that the influence of an attribute in discretization of another attribute depends on the strength of their relationship. 
 * 
 * <h2>Reference</h2>
 * 
 * Rahman, M. G. and Islam, M. Z. (2016): Discretization of Continuous Attributes Through Low Frequency Numerical Values and Attribute Interdependency. Expert Systems with Applications, 45, 410-423. http://dx.doi.org/10.1016/j.eswa.2015.10.005.
 *  
 * @author Md Geaur Rahman <https://csusap.csu.edu.au/~grahman/>
 */
/**
 *
 * @author grahman
 */
public class LFD
{
    private String [][]dataset;//data set
    private int noOfAttrs; // total no. of attributes of the data file
    private int noOfRecords; // total no. of attributes of the data file
    private String [] attrNames; //contain attributes name
    private String [] attrType; // "n"->numerical, "c"->categorical
    private int [] attrNType; // 1->numerical, 0->categorical, 2->class (categorical)
    private int noOfCatAttrs; //no. of categorical attributes
    private int noOfNumAttrs; //no. of Numerical attributes
    private int [][]MV;  //Missing values, 0->no missing, 1->Missing
    private double []RA;// contains average correlations of numerical and categorical attributes
    private int []NumAttrs;//contains index of numerical attributes
    private int []CatAttrs;//contains index of categorical attributes
    private int []domainsize;//contains domain size of each attribute
    private String [][]domainValues;//contains domain values of all attributes
    private double []mean; //contains mean value of each numerical attribute
    private double lambda;
    private int ds;
    private int []fqCount;
    private double []d;
    private double []iCutpoints;
    private double []fCutpoints;
    private long exeTime;
    private double pCatAttrs;
    
    /*
     * this method will discretize numerical attributes
     * 
     * @param attrFile atrtibute file
     * @param dataFile data file having missing values
     * @param outputFile file with discretized data
     */

    public void runLFD(String attrFile, String dataFile,String outputFile)
    {
        FileManager fileManager=new FileManager();
        String [][]tmpAty=fileManager.readFileAs2DArray(new File(attrFile));
        dataset=fileManager.readFileAs2DArray(new File(dataFile));
        double PercentageOfCategoricalAttrs=0.2;
        runDiscretization(dataset,tmpAty,PercentageOfCategoricalAttrs);
        arrayToFile(dataset, noOfRecords, noOfAttrs, outputFile); //write to output file
        
    }
public void runDiscretization(String [][]datasetG,String [][]attrInfo, double PercentageOfAttrs)
{
        long start = System.currentTimeMillis();
        initialize(datasetG,attrInfo,PercentageOfAttrs);
        calAvgEta(RA);//calculates average correlations of numerical and categorical attributes
        rankNAttributes(RA,NumAttrs,noOfNumAttrs);
        discretization(RA,NumAttrs,noOfNumAttrs,CatAttrs, noOfCatAttrs);
        long end = System.currentTimeMillis();
        exeTime=end-start;
}

public int []getIntervals()
{
    return domainsize;
}

public long getExeTime()
{
    return exeTime;
}

/*
 * the method initializes the variables of DACUF
 */
public void initialize(String [][]datasetG,String [][]attrInfo, double PercentageOfAttrs)
    {
        pCatAttrs=PercentageOfAttrs;
        dataset=datasetG;
        noOfRecords=dataset.length;
        noOfAttrs=dataset[0].length;
        attrNType=new int[noOfAttrs];
        attrType=new String[noOfAttrs];
        attrNames=new String[noOfAttrs];
        domainsize=new int[noOfAttrs];
        mean=new double[noOfAttrs];
        domainValues=new String[noOfAttrs][noOfRecords];
        MV=new int[noOfRecords][noOfAttrs];
        fqCount=new int[noOfRecords];
        d=new double[noOfRecords];
        noOfCatAttrs=0;
        noOfNumAttrs=0;
        for(int i=0; i<noOfAttrs;i++)
         {
            attrNames[i]=attrInfo[1][i];
            if(attrInfo[0][i].equals("1"))
             {
                 attrNType[i]=1;
                 attrType[i]="n";
                 noOfNumAttrs++;
             }
            else
             {
                 attrType[i]="c";
                 attrNType[i]=0;
                 noOfCatAttrs++;
             }
         }
        for(int i=0;i<noOfRecords;i++)
        {
            for(int j=0;j<noOfAttrs;j++)
            {
                MV[i][j]=isMissing(dataset[i][j]);
            }
        }
        
        RA=new double[noOfNumAttrs];
        NumAttrs=new int[noOfNumAttrs];
        CatAttrs=new int[noOfCatAttrs];
        int tj=0,tc=0;
        for(int i=0; i<noOfAttrs;i++)
         {
            if(attrNType[i]==1)
            {
               NumAttrs[tj]=i;
               RA[tj]=0.0;
               tj++;
               domainsize[i]=0;
               mean[i]=calMean(i);
            }
            else{
                CatAttrs[tc]=i;
                tc++;
                domainsize[i]=findDomainSize(i);
                mean[i]=0.0;
             }
        }
     
    }

/*
 * Distrization
 */
private void discretization(double []Ra, int []nAttrs, int nA,  int []cAttrs, int nC)
{
    for(int j=0;j<nA;j++)
    {
        int currentAttribute=nAttrs[j];
        frequencyDistribution(currentAttribute);
        SortAsc(d,fqCount,ds);
        iCutpoints=new double[ds];
        int ticp=findInitialCutpoints(d,fqCount,ds,iCutpoints,lambda);
        int []fcFlg=new int[ticp];
        for(int t=0; t<ticp;t++)
        {
            fcFlg[t]=0;
        }
        fcFlg[0]=1;fcFlg[ticp-1]=1;
        int fcp=2;
        double V=0.0;
        int tau=1;
        while(tau==1)
        {
            double mV=0.0;
            int mt=-1;
            for(int t=0; t<ticp;t++)
            {
               if(fcFlg[t]==0)
               {
                  int nc=fcp+1;
                  double []tCP=new double[nc];
                  int y=0;
                  for(int k=0; k<ticp;k++)
                  {
                       if(fcFlg[k]==1||k==t)
                       {
                            tCP[y]=iCutpoints[k];y++;
                       }
                  }
                  double tv=0.0, twCaim=0.0, tUn=0.0;
                  int []catAttr=findCatAttrs(currentAttribute);
                  int numCat=catAttr.length;
                  for(int c=0;c<numCat;c++)
                  {
                      int cIndex=catAttr[c];
                      if(attrNType[cIndex]==0)
                      {
                          double []cu=CAIM_Uncertainty(currentAttribute, cIndex, tCP, nc);
                          twCaim+=cu[1]*cu[0];
                          tUn+=cu[1];
                      }
                  }
                  if(tUn>0.0)tv=twCaim/tUn;
                  if(tv>mV)
                  {
                     mV=tv;mt=t;
                  }

               }
            }
            if(mV>V &&(mt>=0 && mt<ticp))
            {
                fcFlg[mt]=1;fcp++;
                V=mV;
            }
            else{
                tau = 0;
            }
        }
       fCutpoints=new double[fcp];
       int fc=0;
       for(int t=0; t<ticp;t++)
       {
               if(fcFlg[t]==1)
               {
                fCutpoints[fc]=iCutpoints[t];fc++;
               }
        }
        discretizeX(currentAttribute,fCutpoints,fcp);
        attrNType[currentAttribute]=0;
        domainsize[currentAttribute]=fcp-1;
        for(int t=0; t<fcp-1;t++)
        {
            domainValues[currentAttribute][t]=t+"";
        }
    }
}

private void discretizeX(int nAttr, double []cutpoints, int nc)
{
    int NOI=nc-1;
    for(int i=0;i<noOfRecords;i++)
    {
      if(MV[i][nAttr]==0 )
      {
          double nCV=Double.parseDouble(dataset[i][nAttr]);
          String cat="";
          for(int j=0;j<NOI;j++)
          {
                    if(j==0)
                    {
                        if(nCV>=cutpoints[j] && nCV<=cutpoints[j+1] )
                        {
                            cat=j+"";break;
                        }
                    }
                     else{
                        if(nCV>cutpoints[j] && nCV<=cutpoints[j+1] )
                        {
                           cat=j+"";break;
                        }
                     }
          }
         dataset[i][nAttr]=cat;
      }
    }
}

private double []CAIM_Uncertainty(int nAttr, int cAttr, double []cutpoints, int nc)
{
    double []CU=new double[2];
    CU[0]=0.0;
    int NOI=nc-1;
    int cv=domainsize[cAttr];
    int [][]coapp=new int[cv][NOI];
    int M=0;
    int []rowTotal=new int[cv];
    int []colTotal=new int[NOI];
    int []Maxr=new int[NOI];
    //calculate CAIM
    for(int c=0;c<cv;c++)
    {
        String tdv=domainValues[cAttr][c];
        for(int j=0;j<NOI;j++)
        {
            int count=0;
            for(int i=0;i<noOfRecords;i++)
            {
                if(MV[i][nAttr]==0 && MV[i][cAttr]==0)
                {
                    double nCV=Double.parseDouble(dataset[i][nAttr]);
                    if(j==0)
                    {
                        if(nCV>=cutpoints[j] && nCV<=cutpoints[j+1] && tdv.equals(dataset[i][cAttr]) )
                        {
                            count++;
                        }
                    }
                     else{
                        if(nCV>cutpoints[j] && nCV<=cutpoints[j+1] && tdv.equals(dataset[i][cAttr]) )
                        {
                            count++;
                        }
                     }
                }
            }
          coapp[c][j]=count;
        }
    }
    for(int c=0;c<cv;c++)
    {
        int count=0;
        for(int j=0;j<NOI;j++)
        {
            count+=coapp[c][j];
        }
        rowTotal[c]=count;
        M+=count;
    }
    double totCaim=0.0;
    for(int j=0;j<NOI;j++)
    {
        int count=0;
        int mxr=0;
        for(int c=0;c<cv;c++)
        {
            count+=coapp[c][j];
            if(coapp[c][j]>mxr)mxr=coapp[c][j];
        }
        colTotal[j]=count;
        Maxr[j]=mxr;
        if(colTotal[j]>0)totCaim+=Math.pow((double)Maxr[j], 2.0)/colTotal[j];
    }
    if(NOI>0)CU[0]=totCaim/(double)NOI;

     //calculate Uncertainty
    double entrophy=0.0;
    CU[1]=0.0;
    for(int c=0;c<cv;c++)
    {
        for(int j=0;j<NOI;j++)
        {
            double Pcj=coapp[c][j]/(double)M;
            if(Pcj>0)
            {
            entrophy+=Pcj*Math.log(1.0/Pcj);
            }
        }
    }
    CU[1]=entrophy;
    return CU;

}

private int findInitialCutpoints(double dl[], int []fq, int dsl,double []ipoints, double lmbda)
{
    int ticp=0;
    for(int i=0;i<dsl;i++)
    {
        if(fq[i]<lmbda||i==0||i==dsl-1)
        {
           ipoints[ticp]=dl[i];
           ticp++;
        }
    }
    return ticp;
}

private void frequencyDistribution(int currAttr)
{
    int totalValue=0;
    ds=0;
    lambda=0.0;
    for (int i=0;i<noOfRecords;i++)
    {
        if(isMissing(dataset[i][currAttr])==0)
        {
            totalValue++;
            double cv=Double.parseDouble(dataset[i][currAttr]);
            int flg=findDomain(d, ds, cv);
            if(flg<0)
            {
                d[ds]=cv;
                fqCount[ds]=1;
                ds++;
                
            }
             else
            {
                fqCount[flg]++;
            }
        }
    }
  if(ds>0)lambda =(double)totalValue/(double)ds;
  
}

private int findDomain(double []dl, int dsl, double cVal)
{
    int flg=-1;

    for(int i=0;i<dsl;i++)
    {
        if(cVal==dl[i])
        {
            flg=i;break;
        }
    }

    return flg;
}


/*
 * Rank the numerical attributes based on the correlation ratio
 */

private void rankNAttributes(double []Ra, int []nAttrs, int nA)
{
    for(int i=0; i<nA-1;i++)
    {
        for(int j=i+1; j<nA;j++)
        {
            if(Ra[j]>Ra[i])
            {
                double tmp=Ra[j];
                Ra[j]=Ra[i];
                Ra[i]=tmp;
                int tmpA=nAttrs[j];
                nAttrs[j]=nAttrs[i];
                nAttrs[i]=tmpA;
            }
        }
    }

}

/*
 * sort the distinct values of an attribute in accening order.
 */

private void SortAsc(double []Ra, int []nAttrs, int nA)
{
    for(int i=0; i<nA-1;i++)
    {
        for(int j=i+1; j<nA;j++)
        {
            if(Ra[j]<Ra[i])
            {
                double tmp=Ra[j];
                Ra[j]=Ra[i];
                Ra[i]=tmp;
                int tmpA=nAttrs[j];
                nAttrs[j]=nAttrs[i];
                nAttrs[i]=tmpA;
            }
        }
    }

}


/**
  * this function will indicate whether or not a value is missing.
  *
  * @param oStr the string to be checked
  * @return ret an integer value 0->No missing, 1->Missing
  */

 private int isMissing(String oStr)
    {
       int ret=0;
       if(oStr.equals("")||oStr.equals("?")||oStr.equals("�")||oStr.equals("NaN")||oStr.equals("  NaN"))
                     {
                         ret=1;
                    }
       return ret;
    }

/*
 * this method is used to write an array into a file
 *
 */

private void arrayToFile(String [][]data, int totRec, int totAttr,String outF)
{
        FileManager fileManager=new FileManager();
        File outFile=new File(outF);

        for(int i=0;i<totRec;i++)
        {
           String rec="";

           for(int j=0;j<totAttr;j++)
           {
                    rec=rec+data[i][j]+", ";
           }
           if(i<totRec-1)
               rec=rec+"\n";
           if(i==0)
               fileManager.writeToFile(outFile, rec);
           else
               fileManager.appendToFile(outFile, rec);
        }
}

 /*
 * this method is used to find domain size of each attribule
 */
public int findDomainSize(int attrPos)
    {
        int attrDomainSize=0;
        for(int i=0; i<noOfRecords;i++)
        {
           if(MV[i][attrPos]==0)
           {
               if(chkDomain(attrDomainSize,attrPos,dataset[i][attrPos])==0)
                {
                    domainValues[attrPos][attrDomainSize]=dataset[i][attrPos];
                    attrDomainSize++;
                }
            }
        }
        return attrDomainSize;
    }
private int chkDomain(int domainSize,int attrPos, String curVal)
    {
        int flag=0;
        for(int i=0;i<domainSize;i++)
        {
            if(curVal.equals(domainValues[attrPos][i]))
            {
               flag=1; break;
            }
        }
        return flag;
    }
/*
 * Calculate mean value of each numerical attribute
 */
private double calMean(int aPos)
{
    double mu=0.0, sum=0.0;
    for(int i=0; i<noOfRecords;i++)
        {
           if(MV[i][aPos]==0)
           {
               sum+=Double.parseDouble(dataset[i][aPos]);
            }
        }
    if(noOfRecords>0)mu=sum/noOfRecords;
    return mu;
}

/*
 * Calculate mean value of each numerical attribute
 */
private double calConditionalMean(int nPos, int cPos, String cVal, int []xCnt)
{
    double cmu=0.0, sum=0.0;
    int cnt=0;
    for(int i=0; i<noOfRecords;i++)
        {
           if(MV[i][nPos]==0 && MV[i][cPos]==0&& dataset[i][cPos].equals(cVal))
           {
               sum+=Double.parseDouble(dataset[i][nPos]);
               cnt++;
            }
        }
    xCnt[0]=cnt;
    if(cnt>0)cmu=sum/(double)cnt;
    return cmu;
}

private double eta_denominator(int aPos)
{
    double tvar=0.0, var=0.0,svar=0.0;
    for(int i=0; i<noOfRecords;i++)
        {
           if(MV[i][aPos]==0)
           {
               var=(Double.parseDouble(dataset[i][aPos])-mean[aPos]);
               svar=Math.pow(var, 2.0);
               tvar+=svar;
            }
        }
    return tvar;
}

private double eta_numerator(int nPos,int cPos)
{
    double tvar=0.0,svar=0.0;
    int []count=new int[1];
    for(int j=0;j<domainsize[cPos];j++)
    {
        double cmu=calConditionalMean(nPos,cPos,domainValues[cPos][j],count);
        svar=Math.pow(cmu-mean[nPos], 2.0);
        tvar+=(svar*count[0]);
       
    }
    return tvar;
}


private int []findCatAttrs(int numAttr)
{
    int noCat=0;
    for(int i=0;i<noOfAttrs;i++)
    {
        if(attrNType[i]==0)
        {
            noCat++;
        }
    }

    int []tmpcatIndex=new int[noCat];
    double []R=new double[noCat];
    int k=0;
    for(int i=0;i<noOfAttrs;i++)
    {
        if(attrNType[i]==0 && k<noCat)
        {
            R[k]=calEta(numAttr,i);
            tmpcatIndex[k]=i;
            k++;
        }
    }
    rankNAttributes(R,tmpcatIndex,noCat);

    int ncAttr=(int)(pCatAttrs*(double)noCat);

    if(ncAttr<=0) ncAttr=1;
    if(ncAttr>noCat) ncAttr=noCat;
    int []catIndex=new int[ncAttr];
    for(int i=0;i<ncAttr;i++)
    {
        catIndex[i]=tmpcatIndex[i];
    }
   
    return catIndex;
}


/*
 * The method calculates correlation ratio (eta) for each pair of numerical and categorical attributes,
 * then calculates the average eta value.
 */
private void calAvgEta(double []Ra)
{
    for(int i=0;i<noOfNumAttrs;i++)
    {
        double eta_tmp=0.0;Ra[i]=0.0;
        for(int j=0;j<noOfCatAttrs;j++)
        {
            eta_tmp+=calEta(NumAttrs[i],CatAttrs[j]);
        }
        if(noOfCatAttrs>0) ;Ra[i]=eta_tmp/(double)noOfCatAttrs;
    }
}

/*
 * The method calculates correlation ratio (eta) for each pair of numerical and categorical attributes
 */
private double calEta(int nAttr, int cAttr)
{
    double eta=0.0, denom=0.0;
    denom=eta_denominator(nAttr);
    if(denom>0)
    {
        double numerator=eta_numerator(nAttr,cAttr);
        eta=Math.sqrt(numerator/denom);
    }
//    System.out.println("Between "+nAttr+" and "+cAttr+" is: "+eta);
    return eta;
}





}
