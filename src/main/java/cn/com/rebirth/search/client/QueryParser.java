/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-search-client QueryParser.java 2012-7-6 16:00:24 l.xue.nong$$
 */
package cn.com.rebirth.search.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import cn.com.rebirth.commons.utils.TemplateMatcher;
import cn.com.rebirth.search.core.index.query.BaseQueryBuilder;
import cn.com.rebirth.search.core.index.query.BoolQueryBuilder;
import cn.com.rebirth.search.core.index.query.QueryBuilder;
import cn.com.rebirth.search.core.index.query.QueryBuilders;
import cn.com.rebirth.search.core.index.query.QueryParsingException;
import cn.com.rebirth.search.core.index.query.QueryStringQueryBuilder;
import cn.com.rebirth.search.core.index.query.QueryStringQueryBuilder.Operator;
import cn.com.rebirth.search.core.index.query.RangeQueryBuilder;

import com.google.common.collect.Lists;

/**
 * The Class QueryParser.
 *
 * @author l.xue.nong
 */
public class QueryParser {

	/** The default operator. */
	private static String DEFAULT_OPERATOR = "${default_operator}";

	/** The default_op. */
	public static String default_op = "and";

	/**
	 * Default operator.
	 *
	 * @return the operator
	 */
	public static Operator defaultOperator() {
		String op = new TemplateMatcher("${", "}").replace(DEFAULT_OPERATOR, new TemplateMatcher.VariableResolver() {

			@Override
			public String resolve(String variable) {
				return System.getProperty(variable, default_op);
			}
		});
		if (StringUtils.isBlank(op)) {
			op = default_op;
		}
		if ("or".equalsIgnoreCase(op)) {
			return Operator.OR;
		} else if ("and".equalsIgnoreCase(op)) {
			return Operator.AND;
		} else {
			throw new QueryParsingException(null, "Query default operator [" + op + "] is not allowed");
		}
	}

	/**
	 * Parses the.
	 *
	 * @param queryString the query string
	 * @return the query builder
	 */
	public static QueryBuilder parse(String queryString) {
		ExpressionParser expressionParser = new ExpressionParser();
		return expressionParser.parserExp(queryString);
	}

	/**
	 * Parses the.
	 *
	 * @param queryString the query string
	 * @param repleas the repleas
	 * @return the query builder
	 */
	public static QueryBuilder parse(String queryString, String... repleas) {
		ExpressionParser expressionParser = new ExpressionParser();
		return expressionParser.parserExp(queryString, repleas);
	}

	/**
	 * The Class ExpressionParser.
	 *
	 * @author l.xue.nong
	 */
	static class ExpressionParser {

		//public static final String LUCENE_SPECIAL_CHAR = "&&||-()':={}[],";

		/** The elements. */
		protected List<Element> elements = new ArrayList<Element>();

		/** The querys. */
		protected Stack<BaseQueryBuilder> querys = new Stack<BaseQueryBuilder>();

		/** The operates. */
		protected Stack<Element> operates = new Stack<Element>();

		/**
		 * Instantiates a new expression parser.
		 */
		public ExpressionParser() {
		}

		/**
		 * Gets the elements.
		 *
		 * @return the elements
		 */
		public List<Element> getElements() {
			return elements;
		}

		/**
		 * Sets the elements.
		 *
		 * @param elements the new elements
		 */
		public void setElements(List<Element> elements) {
			this.elements = elements;
		}

		/**
		 * Parser exp.
		 *
		 * @param expression the expression
		 * @param repleas the repleas
		 * @return the query builder
		 */
		public QueryBuilder parserExp(String expression, String... repleas) {
			QueryBuilder lucenceQuery = null;
			try {
				//文法解析
				this.splitElements(expression, repleas);
				//语法解析
				this.parseSyntax();
				if (this.querys.size() == 1) {
					lucenceQuery = this.querys.pop();
				} else {
					throw new IllegalStateException("表达式异常： 缺少逻辑操作符 或 括号缺失");
				}
			} finally {
				elements.clear();
				querys.clear();
				operates.clear();
			}
			return lucenceQuery;
		}

		/**
		 * Split elements.
		 *
		 * @param expression the expression
		 * @param repleas the repleas
		 */
		protected void splitElements(String expression, String... repleas) {
			if (expression == null) {
				return;
			}
			Element curretElement = null;

			char[] expChars = expression.toCharArray();
			for (int i = 0; i < expChars.length; i++) {
				switch (expChars[i]) {
				case '&':
					if (curretElement == null) {
						curretElement = new Element();
						curretElement.type = '&';
						curretElement.append(expChars[i]);
					} else if (curretElement.type == '&') {
						curretElement.append(expChars[i]);
						this.elements.add(curretElement);
						curretElement = null;
					} else if (curretElement.type == '\'') {
						curretElement.append(expChars[i]);
					} else {
						this.elements.add(curretElement);
						curretElement = new Element();
						curretElement.type = '&';
						curretElement.append(expChars[i]);
					}
					break;

				case '|':
					if (curretElement == null) {
						curretElement = new Element();
						curretElement.type = '|';
						curretElement.append(expChars[i]);
					} else if (curretElement.type == '|') {
						curretElement.append(expChars[i]);
						this.elements.add(curretElement);
						curretElement = null;
					} else if (curretElement.type == '\'') {
						curretElement.append(expChars[i]);
					} else {
						this.elements.add(curretElement);
						curretElement = new Element();
						curretElement.type = '|';
						curretElement.append(expChars[i]);
					}
					break;

				case '-':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '-';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case '(':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '(';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case ')':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = ')';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case ':':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = ':';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case '=':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '=';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case ' ':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
						} else {
							this.elements.add(curretElement);
							curretElement = null;
						}
					}

					break;

				case '\'':
					if (curretElement == null) {
						curretElement = new Element();
						curretElement.type = '\'';

					} else if (curretElement.type == '\'') {
						this.elements.add(curretElement);
						curretElement = null;

					} else {
						this.elements.add(curretElement);
						curretElement = new Element();
						curretElement.type = '\'';

					}
					break;

				case '[':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '[';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case ']':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = ']';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;

					break;

				case '{':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '{';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;
					break;

				case '}':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = '}';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;

					break;
				case ',':
					if (curretElement != null) {
						if (curretElement.type == '\'') {
							curretElement.append(expChars[i]);
							continue;
						} else {
							this.elements.add(curretElement);
						}
					}
					curretElement = new Element();
					curretElement.type = ',';
					curretElement.append(expChars[i]);
					this.elements.add(curretElement);
					curretElement = null;

					break;

				default:
					if (curretElement == null) {
						curretElement = new Element();
						curretElement.type = 'F';
						curretElement.append(expChars[i]);

					} else if (curretElement.type == 'F') {
						curretElement.append(expChars[i]);

					} else if (curretElement.type == '\'') {
						curretElement.append(expChars[i]);

					} else {
						this.elements.add(curretElement);
						curretElement = new Element();
						curretElement.type = 'F';
						curretElement.append(expChars[i]);
					}
				}
			}

			if (curretElement != null) {
				this.elements.add(curretElement);
				curretElement = null;
			}
			if (repleas != null) {
				for (int r = 0; r < repleas.length; r++) {
					if (StringUtils.isNotBlank(repleas[r])) {
						String replea = repleas[r].trim();

						for (int j = 0; j < this.elements.size(); j++) {
							Element element = this.elements.get(j);
							if (element != null && replea.equals(element.toString())) {
								int k = j;
								while (k >= 0 && true) {
									elements.set(k, null);
									if (k <= 0)
										break;
									k--;
									Element element2 = elements.get(k);
									if (element2 == null || element2.type == ')' || element2.type == 'F'
											|| element2.type == '\'' || element2.type == '}' || element2.type == ']')
										break;
								}
								int h = j;
								while (h >= 0 && true && h < this.elements.size()) {
									elements.set(h, null);
									h++;
									if (h >= this.elements.size())
										break;
									Element element2 = this.elements.get(h);
									if (j == 0 || findToNull(j)) {
										if (element2.type == '(' || element2.type == 'F') {
											break;
										}
									} else {
										if ((element2.type == '(' || element2.type == 'F' || element2.type == ' '
												|| element2.type == '&' || element2.type == '|')) {
											break;
										}
									}
								}
							}
						}
						List<Element> e = Lists.newArrayList(this.elements);
						this.elements.clear();
						for (Element element : e) {
							if (element != null) {
								this.elements.add(element);
							}
						}
					}
				}
			}
		}

		/**
		 * Find to null.
		 *
		 * @param j the j
		 * @return true, if successful
		 */
		private boolean findToNull(int j) {
			while (j >= 0) {
				Element element = this.elements.get(j);
				if (element != null) {
					return false;
				}
				j--;
			}
			return true;
		}

		/**
		 * Optimize queries.
		 *
		 * @param queries the queries
		 * @return the base query builder
		 */
		protected static BaseQueryBuilder optimizeQueries(List<BaseQueryBuilder> queries) {
			if (queries.size() == 0) {
				return null;
			} else if (queries.size() == 1) {
				return queries.get(0);
			} else {
				BoolQueryBuilder mustQueries = QueryBuilders.boolQuery();
				for (BaseQueryBuilder baseQueryBuilder : queries) {
					mustQueries.must(baseQueryBuilder);
				}
				return mustQueries;
			}
		}

		/**
		 * Parses the syntax.
		 */
		protected void parseSyntax() {
			for (int i = 0; i < this.elements.size(); i++) {
				Element e = this.elements.get(i);
				if ('F' == e.type) {
					Element e2 = this.elements.get(i + 1);
					if ('=' != e2.type && ':' != e2.type) {
						throw new IllegalStateException("表达式异常： = 或 ： 号丢失");
					}
					Element e3 = this.elements.get(i + 2);
					//处理 = 和 ： 运算
					if ('\'' == e3.type) {
						i += 2;
						if ('=' == e2.type) {
							this.querys.push(QueryBuilders.termQuery(e.toString(), e3.toString()));
						} else if (':' == e2.type) {
							String keyword = e3.toString();
							if (keyword.startsWith("^") && keyword.endsWith("$")) {
								QueryStringQueryBuilder pQuery = this.luceneQueryParse(e.toString(), keyword);
								this.querys.push(pQuery);
							} else {
								//								List<BaseQueryBuilder> builders = Lists.newArrayList();
								//								SumMallAnalyzers analyzers = SumMallAnalyzersFactory.getSumMallAnalyzers();
								//								TokenStream tr = analyzers.tokenStream(e.toString(), new StringReader(e3.toString()));
								//								try {
								//									while (tr.incrementToken()) {
								//										String key = tr.getAttribute(CharTermAttribute.class).toString();
								//										builders.add(QueryBuilders.fieldQuery(e.toString(), key));
								//									}
								//								} catch (Exception e4) {
								//									e4.printStackTrace();
								//								}
								this.querys.push(QueryBuilders.queryString(e.toString() + ":\"" + keyword + "\"")
										.defaultOperator(defaultOperator()));
							}
						}

					} else if ('[' == e3.type || '{' == e3.type) {
						i += 2;
						//处理 [] 和 {}
						LinkedList<Element> eQueue = new LinkedList<Element>();
						eQueue.add(e3);
						for (i++; i < this.elements.size(); i++) {
							Element eN = this.elements.get(i);
							eQueue.add(eN);
							if (']' == eN.type || '}' == eN.type) {
								break;
							}
						}
						//翻译RangeQuery
						RangeQueryBuilder rangeQuery = this.toTermRangeQuery(e, eQueue);
						this.querys.push(rangeQuery);
					} else {
						throw new IllegalStateException("表达式异常：匹配值丢失");
					}

				} else if ('(' == e.type) {
					this.operates.push(e);

				} else if (')' == e.type) {
					boolean doPop = true;
					while (doPop && !this.operates.empty()) {
						Element op = this.operates.pop();
						if ('(' == op.type) {
							doPop = false;
						} else {
							BaseQueryBuilder q = toQuery(op);
							this.querys.push(q);
						}

					}
				} else {
					if (this.operates.isEmpty()) {
						this.operates.push(e);
					} else {
						boolean doPeek = true;
						while (doPeek && !this.operates.isEmpty()) {
							Element eleOnTop = this.operates.peek();
							if ('(' == eleOnTop.type) {
								doPeek = false;
								this.operates.push(e);
							} else if (compare(e, eleOnTop) == 1) {
								this.operates.push(e);
								doPeek = false;
							} else if (compare(e, eleOnTop) == 0) {
								BaseQueryBuilder q = toQuery(eleOnTop);
								this.operates.pop();
								this.querys.push(q);
							} else {
								BaseQueryBuilder q = toQuery(eleOnTop);
								this.operates.pop();
								this.querys.push(q);
							}
						}

						if (doPeek && this.operates.empty()) {
							this.operates.push(e);
						}
					}
				}
			}

			while (!this.operates.isEmpty()) {
				Element eleOnTop = this.operates.pop();
				BaseQueryBuilder q = toQuery(eleOnTop);
				this.querys.push(q);
			}
		}

		/**
		 * To query.
		 *
		 * @param op the op
		 * @return the base query builder
		 */
		protected BaseQueryBuilder toQuery(Element op) {
			if (this.querys.size() == 0) {
				return null;
			}
			BoolQueryBuilder resultQuery = QueryBuilders.boolQuery();
			if (this.querys.size() == 1) {
				return this.querys.get(0);
			}

			BaseQueryBuilder q2 = this.querys.pop();
			BaseQueryBuilder q1 = this.querys.pop();
			if ('&' == op.type) {
				if (q1 != null) {
					resultQuery.must(q1);
				}
				if (q2 != null) {
					resultQuery.must(q2);
				}

			} else if ('|' == op.type) {
				if (q1 != null) {
					resultQuery.should(q1);
				}

				if (q2 != null) {
					resultQuery.should(q2);
				}
			} else if ('-' == op.type) {
				if (q1 == null || q2 == null) {
					throw new IllegalStateException("表达式异常：SubQuery 个数不匹配");
				}
				resultQuery.must(q1);
				resultQuery.mustNot(q2);
			}
			return resultQuery;
		}

		/**
		 * To term range query.
		 *
		 * @param fieldNameEle the field name ele
		 * @param elements the elements
		 * @return the range query builder
		 */
		protected RangeQueryBuilder toTermRangeQuery(Element fieldNameEle, LinkedList<Element> elements) {
			boolean includeFirst = false;
			boolean includeLast = false;
			String firstValue = null;
			String lastValue = null;
			//检查第一个元素是否是[或者{
			Element first = elements.getFirst();
			if ('[' == first.type) {
				includeFirst = true;
			} else if ('{' == first.type) {
				includeFirst = false;
			} else {
				throw new IllegalStateException("表达式异常");
			}
			//检查最后一个元素是否是]或者}
			Element last = elements.getLast();
			if (']' == last.type) {
				includeLast = true;
			} else if ('}' == last.type) {
				includeLast = false;
			} else {
				throw new IllegalStateException("表达式异常, RangeQuery缺少结束括号");
			}
			if (elements.size() < 4 || elements.size() > 5) {
				throw new IllegalStateException("表达式异常, RangeQuery 错误");
			}
			//读出中间部分
			Element e2 = elements.get(1);
			if ('\'' == e2.type) {
				firstValue = e2.toString();
				//
				Element e3 = elements.get(2);
				if (',' != e3.type) {
					throw new IllegalStateException("表达式异常, RangeQuery缺少逗号分隔");
				}
				//
				Element e4 = elements.get(3);
				if ('\'' == e4.type) {
					lastValue = e4.toString();
				} else if (e4 != last) {
					throw new IllegalStateException("表达式异常，RangeQuery格式错误");
				}
			} else if ('F' == e2.type) {
				firstValue = e2.toString();
				//
				Element e3 = elements.get(2);
				if (',' != e3.type) {
					throw new IllegalStateException("表达式异常, RangeQuery缺少逗号分隔");
				}
				//
				Element e4 = elements.get(3);
				lastValue = e4.toString();
			} else if (',' == e2.type) {
				firstValue = null;
				//
				Element e3 = elements.get(2);
				if ('\'' == e3.type) {
					lastValue = e3.toString();
				} else {
					throw new IllegalStateException("表达式异常，RangeQuery格式错误");
				}

			} else {
				throw new IllegalStateException("表达式异常, RangeQuery格式错误");
			}
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(fieldNameEle.toString());
			rangeQueryBuilder.from(Integer.valueOf(firstValue)).to(Integer.valueOf(lastValue))
					.includeLower(includeFirst).includeUpper(includeLast);
			return rangeQueryBuilder;
		}

		/**
		 * Lucene query parse.
		 *
		 * @param fieldName the field name
		 * @param keyword the keyword
		 * @return the query string query builder
		 */
		protected QueryStringQueryBuilder luceneQueryParse(String fieldName, String keyword) {
			//截取头部^尾部$
			keyword = keyword.substring(1, keyword.length() - 1);
			String luceneExp = fieldName + ":\"" + keyword + "\"";
			QueryStringQueryBuilder queryBuilder = QueryBuilders.queryString(luceneExp).defaultOperator(
					defaultOperator());
			return queryBuilder;
		}

		/**
		 * Compare.
		 *
		 * @param e1 the e1
		 * @param e2 the e2
		 * @return the int
		 */
		public static int compare(Element e1, Element e2) {
			if ('&' == e1.type) {
				if ('&' == e2.type) {
					return 0;
				} else {
					return 1;
				}
			} else if ('|' == e1.type) {
				if ('&' == e2.type) {
					return -1;
				} else if ('|' == e2.type) {
					return 0;
				} else {
					return 1;
				}
			} else {
				if ('-' == e2.type) {
					return 0;
				} else {
					return -1;
				}
			}
		}

		/**
		 * The Class Element.
		 *
		 * @author l.xue.nong
		 */
		class Element {

			/** The type. */
			char type = 0;

			/** The ele text buff. */
			StringBuffer eleTextBuff;

			/**
			 * Instantiates a new element.
			 */
			public Element() {
				eleTextBuff = new StringBuffer();
			}

			/**
			 * Append.
			 *
			 * @param c the c
			 */
			public void append(char c) {
				this.eleTextBuff.append(c);
			}

			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString() {
				return this.eleTextBuff.toString();
			}

		}
	}
}
