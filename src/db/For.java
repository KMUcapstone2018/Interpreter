package db;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import common.Pair;

public class For extends Type{
	
	public For() {
		typeName = "for";
		originalFormat = "for(int ?=?;?<=?;?++)";
		addMatchedFormat(".*(for)\\([a-zA-Z]+=\\w+~\\w+\\).*", "[(for\\()|=|~|\\)]");		// for(i = 0 ~ N)
		addMatchedFormat(".*(for)\\([a-zA-Z]+=\\w+->\\w+\\).*", "[(for\\()=->\\)]");	// for(i = 0 -> N)
		addMatchedFormat(".*(for)\\([a-zA-Z]+->\\w+:\\w+\\).*", "[(for\\()->\\:\\)]");	// for(i -> 0 : N)
	}
	
	@Override
	public String convert(String codeLine) {
		String lines = codeLine.replaceAll(" ", "");
		StringBuilder converted = new StringBuilder(originalFormat.length());
		
		for(Pair<String, String> each : matchedFormat) {
			if(lines.matches(each.format) == false) continue;

			lines = lines.replaceAll(each.preserved, " ");
			List<String> varList = Arrays.stream(lines.split(" "))
					.filter(x-> x.length() > 0)
					.collect(Collectors.toList());
			String vars[] = varList.toArray(new String[varList.size()]);
			
			/*
			 * vars[0] = i
			 * vars[1] = 0
			 * vars[2] = N
			 */
			int endOfVarIdx = 2;
			int i, j, len = originalFormat.length(), seq[] = {0, 1, 0, 2, 0};
			char ch;
			for(i=j=0;i<len;i++) {
				ch = originalFormat.charAt(i);
				converted.append(ch == '?' ? vars[seq[j++]] : ch);
			}

			for(i=endOfVarIdx+1; i<vars.length;i++) converted.append(vars[i]);
			
			converted.append("\n");
			break;
		}
		
		return converted.toString();
	}

}
